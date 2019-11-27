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

import static utilities.strings.get_extension;

public class encoding_handler {
    technique naive = new Naive(), bpcs = new BPCS();

    public static void main(String[] args) {
        Path dir_path = Paths.get("processed");
        File[] files = get_candidates(dir_path);

        for (File file : files)
            System.out.println(file);

        encoding_handler handler = new encoding_handler();
        Potential[] potentials = handler.check_candidates(files);

        for (Potential potential : potentials)
            System.out.println(potential);
    }

    public Potential[] check_candidates(File[] candidates) {
        ArrayList<Potential> potentials = new ArrayList<>();
        int index = 0;

        for (File filename : candidates) {
            image img = new image(filename.toString(), naive);
            System.out.printf("Encode mode %d\n", img.encode_mode);

            if (img.encode_mode == 0) {
                potentials.add(new Potential(++index, false, img));
                continue;
            }
            img.load_image(bpcs);
            if (img.encode_mode == 1) {
                int key = img.encoding_id;
                if (potentials.contains(key))
                    potentials.get(key).add(img);
                else
                    potentials.add(new Potential(++index, true, img, key));
            }
        }

        return (Potential[]) potentials.toArray();
    }

//    public String[] check_candidates(String[] candidates) {
//        HashMap<Integer, ArrayList<image>> advanced_map = new HashMap<>();
//        HashMap<Integer, image> basic_map = new HashMap<>();
//        int basic_index = 0;
//
//        for (String filename : candidates) {
//            image img = new image(filename, naive);
//            if (img.encode_mode == 0) {
//                basic_map.put(++basic_index, img);
//                continue;
//            }
//            img.load_image(bpcs);
//            if (img.encode_mode == 1) {
//                int key = img.encoding_id;
//
//                if (!advanced_map.containsKey(key))     // If key isn't present, add it
//                    advanced_map.put(key, new ArrayList<image>());
//                advanced_map.get(key).add(img);         // Add image to entry
//            }
//        }
//    }

    final static FilenameFilter filter = ((dir, name) -> get_extension(name).equals("png"));

    public static File[] get_candidates(Path directory) {
        File dir_file = directory.toFile();

        return dir_file.listFiles(filter);
    }
}
