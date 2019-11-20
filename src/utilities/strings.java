package utilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class strings {
    // Executes a regex function
    public static String execute_regex(String regex_string, String target) {
        Pattern pattern = Pattern.compile(regex_string);
        Matcher matcher = pattern.matcher(target);

        return execute_regex(matcher);
    }

    // Gets the file type extension (ex. file.txt -> txt)
    public static String get_extension(String filename) {
        return execute_regex("(?<=\\.)\\w+", filename);
    }

    // Removes extension from filename (ex. file.txt -> file)
    public static String remove_extension(String filename) {
        return execute_regex("^[\\w\\d-]+(?=\\.)", filename);
    }

    // Executes regex function
    public static String execute_regex(Matcher matcher) {
        boolean did_find = matcher.find();
        if (!did_find)
            return null;

        return matcher.group(0);
    }
}
