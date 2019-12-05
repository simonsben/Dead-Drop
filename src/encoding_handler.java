import core.BPCS;
import core.Naive;
import core.Image;
import core.technique;
import core.Potential;
import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import static core.header.decode_header;
import static utilities.data_management.compute_md5;
import static utilities.input.get_input;
import static utilities.input.load_file;
import static utilities.output.read_out;
import static utilities.output.write_file;
import static utilities.strings.get_extension;

public class encoding_handler {
    Potential selected;
    HashMap<Short, Potential> potentials;
    technique naive = new Naive(), bpcs = new BPCS();
    String source_directory, target_filename, target_directory;
    boolean will_encode, is_basic, is_naive;
    static String manual = "Expected parameters as:\n" +
            "-E/D B/A mode of operations (i.e. encrypt or decode, for encode specify Basic/Advanced)\n" +
            "-T N/B technique (i.e. Naive or BPCS encoding technique)\n" +
            "-s source directory (i.e. location of image files)\n" +
            "-d data file (i.e. path for the data/recovered file)\n" +
            "-t target directory (if encoding, directory to save images to)\n";

    public static void main(String[] args) {
        encoding_handler handler = new encoding_handler();
        handler.get_params(args);

        if (!handler.will_encode) {
            handler.select_potential();
            handler.decode_selection();
        }
    }

    void get_params(String[] args) {
        for (int index = 0; index < args.length - 1; index++) {
            if (args[index].charAt(0) != '-') continue;
            if (args[index].equals("-s"))
                source_directory = args[index + 1];
            else if (args[index].equals("-d"))
                target_filename = args[index + 1];
            else if (args[index].equals("-E") || args[index].equals("-D")) {        // Assumes decode by default
                will_encode = args[index].equals("-E");
                if (args[index + 1].charAt(0) != '-')
                    is_basic = args[index + 1].equals("B");
            }
            else if (args[index].equals("-t"))
                target_directory = args[index + 1];
            else if (will_encode && args[index].equals("-T")) {
                if (args[index + 1].charAt(0) != '-')
                    is_basic = args[index + 1].equals("B");
            }
        }

        if (source_directory == null)
            throw new IllegalArgumentException("Source directory not provided.\n" + manual);
        if (!(new File(source_directory).exists()))
            throw new IllegalArgumentException("Source directory doesn't exist.\n" + manual);
        if (target_filename == null)
            throw new IllegalArgumentException("Data filename must be provided.\n" + manual);
        if (will_encode && !(new File(target_filename)).exists())
            throw new IllegalArgumentException("Data file does not exist.\n" + manual);
        if (will_encode && target_directory == null)
            throw new IllegalArgumentException("Target directory not provided.\n" + manual);
        if (will_encode && !(new File(target_directory).exists()))
            throw new IllegalArgumentException("Target directory doesn't exist.\n" + manual);

        get_potentials();
    }

    void get_potentials() {
        Path dir_path = Paths.get(source_directory);
        File[] files = get_candidates(dir_path);

        if (!will_encode)
            potentials = check_candidates(files);
        else {
            System.out.println("Available images:");
            for (int index = 0; index < files.length; index++)
                System.out.printf("%d %s\n", index, files[index].getName());
            encode_selection(files);
        }
    }

    void encode_selection(File[] files) {
        String[] filenames = new String[files.length];
        for (int index = 0; index < files.length; index++)
            filenames[index] = files[index].toString();

        String tech = is_basic? "naive" : "bpcs";
        image_encoder encoder = is_basic? new basic_encoder(filenames, tech) : new advanced_encoder(filenames, tech);

        byte[] file_data = load_file(target_filename);
        encoder.encode_data(file_data);
        encoder.save_images(target_directory);
    }

    void decode_selection() {
        if (selected == null)
            throw new IllegalCallerException("Can't decode data without a valid selection");
        Image[] selected_images = selected.image_set.toArray(new Image[selected.image_set.size()]);
        technique tech = selected_images[0].encode_tech == 0? naive : bpcs;

        image_encoder encoder;
        if (selected.is_advanced) encoder = new advanced_encoder(selected_images, tech, selected.encoding_id);
        else encoder = new basic_encoder(selected_images, tech);

        byte[] recovered = encoder.decode_data();
        write_file(target_filename, recovered);
    }

    public void select_potential() {
        System.out.println(this);
        System.out.println("Enter the entry index or encoding ID");
        short index = get_input();              // Get user index selection
        if (potentials.containsKey(index))      // If basic get encoding
            selected = potentials.get(index);
        else                                    // If advanced get encoding
            selected = potentials.values().stream()
                    .filter(potential -> potential.index == index).findFirst()
                    .orElse(null);

        // If index is invalid, try again
        if (selected == null) {
            System.out.println("Invalid index, try again.");
            select_potential();
            return;
        }
        System.out.print("Selected ");
        System.out.println(selected);
    }

    public HashMap<Short, Potential> check_candidates(File[] candidates) {
        HashMap<Short, Potential> potentials = new HashMap<>();
        short index = 0;

        for (File filename : candidates) {
            Image img = new Image(filename.toString(), naive);      // Try naive
            if (!img.was_used) decode_header(img, bpcs);            // Try bpcs
            if (!img.was_used) continue;                            // Unused image

            if (img.encode_mode == 0)           // If basic
                potentials.put(index, new Potential(index++, false, img));
            else if (img.encode_mode == 1) {    // If advanced
                short key = img.encoding_id;
                if (potentials.containsKey(key))
                    potentials.get(key).add(img);
                else
                    potentials.put(key, new Potential(index++, true, img, key));
            }
        }

        return potentials;
    }

    static HashSet<String> source_filetypes = new HashSet<>(Arrays.asList("png", "jpg"));

    final static FilenameFilter decode_filter = ((dir, name) -> get_extension(name).equals("png"));
    final static FilenameFilter encode_filter = ((dir, name) -> source_filetypes.contains(get_extension(name)));

    public File[] get_candidates(Path directory) {
        File dir_file = directory.toFile();

        return dir_file.listFiles(will_encode? encode_filter : decode_filter);
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append("Potential candidates:\n");

        potentials.forEach((key, potential) -> {
            output.append(potential);
            output.append('\n');
        });

        return output.toString();
    }
}
