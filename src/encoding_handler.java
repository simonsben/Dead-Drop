import core.BPCS;
import core.Naive;
import core.image;
import core.technique;
import core.Potential;
import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static utilities.strings.get_extension;

public class encoding_handler {
    technique naive = new Naive(), bpcs = new BPCS();
    HashMap<Short, Potential> potentials;

    public static void main(String[] args) {
        encoding_handler handler = new encoding_handler("processed");
        System.out.println(handler);
    }

    public encoding_handler(String directory) {
        Path dir_path = Paths.get(directory);
        File[] files = get_candidates(dir_path);
        potentials = check_candidates(files);
    }

    public HashMap<Short, Potential> check_candidates(File[] candidates) {
//        ArrayList<Potential> potentials = new ArrayList<>();
        HashMap<Short, Potential> potentials = new HashMap<>();
        short index = 0;

        for (File filename : candidates) {
            image img = new image(filename.toString(), naive);

            if (img.encode_mode == 0) {     // If basic
                potentials.put(++index, new Potential(index, false, img));
                continue;
            }
            img.load_image(bpcs);
            if (img.encode_mode == 1) {     // If advanced
                short key = img.encoding_id;
                if (potentials.containsKey(key))
                    potentials.get(key).add(img);
                else
                    potentials.put(key, new Potential(++index, true, img, key));
            }
        }

        return potentials;
    }

    final static FilenameFilter filter = ((dir, name) -> get_extension(name).equals("png"));

    public static File[] get_candidates(Path directory) {
        File dir_file = directory.toFile();

        return dir_file.listFiles(filter);
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append("Potential candidates:\n");

        potentials.forEach((key, potential) -> {
            output.append(potential);
            output.append('\n');
        });
//        for (Potential potential : potentials.forEach();) {
//            output.append(potential);
//            output.append('\n');
//        }

        return output.toString();
    }
}
