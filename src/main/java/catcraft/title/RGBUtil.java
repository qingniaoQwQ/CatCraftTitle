package catcraft.title;

public class RGBUtil {

    public static String gradient(String text) {
        StringBuilder result = new StringBuilder();

        String[] colors = {
                "§x§F§F§0§0§0§0",
                "§x§F§F§4§0§F§F",
                "§x§8§0§4§0§F§F"
        };

        int colorIndex = 0;

        for (char c : text.toCharArray()) {
            result.append(colors[colorIndex % colors.length]).append(c);
            colorIndex++;
        }

        return result.toString();
    }
}