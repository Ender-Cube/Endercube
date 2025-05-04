package net.endercube.gamelib.utils;

// Credit: https://www.spigotmc.org/threads/free-code-sending-perfectly-centered-chat-message.95872/

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.stream.Collectors;

/**
 * Utilities for Components
 */
public final class ComponentUtils {

    private ComponentUtils() {
        //not called
    }

    private final static int CENTER_PX = 154;

    /**
     * @author Apache Commons
     */
    public static String capitalize(String string) {
        int length = string.length();

        if (length == 0) {
            return string;
        } else {
            int firstCodepoint = string.codePointAt(0);
            int newCodePoint = Character.toTitleCase(firstCodepoint);

            if (firstCodepoint == newCodePoint) {
                return string;
            } else {
                int[] newCodePoints = new int[length];
                int outOffset = 0;
                newCodePoints[outOffset++] = newCodePoint;
                int codePoint;

                for(int inOffset = Character.charCount(firstCodepoint); inOffset < length; inOffset += Character.charCount(codePoint)) {
                    codePoint = string.codePointAt(inOffset);
                    newCodePoints[outOffset++] = codePoint;
                }

                return new String(newCodePoints, 0, outOffset);
            }
        }
    }

    public static Component getTitle(Component text) {
        final int LINE_SIZE = 10;
        return centerComponent(Component.empty()
                .append(getLine(LINE_SIZE).color(NamedTextColor.BLUE))
                .append(Component.space())
                .append(text)
                .append(Component.space())
                .append(getLine(LINE_SIZE).color(NamedTextColor.BLUE))
                .append(Component.newline())
        );
    }

    public static Component getLine(int size) {
        String spaces = " ".repeat(size);
        return Component.text(spaces).decorate(TextDecoration.STRIKETHROUGH);
    }

    public static Component convertToSmallCaps(Component input) {
        return input.replaceText(
                TextReplacementConfig.builder()
                        .match(".*")
                        .replacement((match, original) -> Component.text(convertToSmallCaps(match.group())))
                        .build()
        );
    }

    /**
     * @see <a href="https://discord.com/channels/706185253441634317/1001092833677152397/1326883302455640104">Discord</a>
     */
    public static String convertToSmallCaps(String string) {
        var input = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        var output = "₀₁₂₃₄₅₆₇₈₉ᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘǫʀꜱᴛᴜᴠᴡxʏᴢᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘǫʀꜱᴛᴜᴠᴡxʏᴢ";

        return string.chars()
                .mapToObj(character -> "" + (input.indexOf(character) < 0
                        ? (char) character
                        : output.charAt(input.indexOf(character))))
                .collect(Collectors.joining());
    }

    /**
     * Adds ordinals(st,nd,rd,th to a number)
     *
     * @param number The number to apply
     * @return A component containing the number and ordinal (e.g: 1st)
     */
    public static Component addOrdinals(int number) {
        final String prefix;

        if (number >= 11 && number <= 13) {
            return Component.text(number + "th");
        }

        switch (number % 10) {
            case 1 -> prefix = "st";
            case 2 -> prefix = "nd";
            case 3 -> prefix = "rd";
            default -> prefix = "th";
        }

        return Component.text(number + prefix);
    }

    /**
     * Changes a number of milliseconds to the HH:mm:ss.SSS format
     *
     * @param milliseconds The number of milliseconds
     * @return HH:mm:ss.SSS formatted String
     */
    public static String toHumanReadableTime(long milliseconds) {
        Date date = new Date(milliseconds);

        // formatter
        SimpleDateFormat formatter = new SimpleDateFormat("H:m:s.SSS");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        return yeet0s(formatter.format(date)) + "s";

    }

    private static String yeet0s(String input) {
        String[] outString = input.split("\\b0:(?=\\d)");

        return outString[outString.length - 1];
    }

    /**
     * Centers a component
     *
     * @param component The component to be centered
     * @return The component prefixed with spaces
     */
    // Yes, I know it is very scuffed
    public static @NotNull Component centerComponent(final @NotNull Component component) {
        return Component.text(spacePrefix(LegacyComponentSerializer.legacySection().serialize(component)))
                .append(component);
    }

    public static @NotNull String spacePrefix(final @NotNull String legacyTextMessage) {
        return spacePrefix(measureLegacyText(legacyTextMessage));
    }

    private static int measureLegacyText(final @NotNull String legacyTextMessage) {
        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for (final char c : legacyTextMessage.toCharArray()) {
            if (c == '§') {
                previousCode = true;
            } else if (previousCode) {
                previousCode = false;
                isBold = c == 'l' || c == 'L';
            } else {
                final DefaultFontInfo info = DefaultFontInfo.forCharacter(c);
                messagePxSize += info.length(isBold);
                messagePxSize++;
            }
        }
        return messagePxSize;
    }

    private static @NotNull String spacePrefix(final int messagePxSize) {
        final int halvedMessageSize = messagePxSize / 2;
        final int toCompensate = CENTER_PX - halvedMessageSize;
        final int spaceLength = DefaultFontInfo.SPACE.length() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while (compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }
        return sb.toString();
    }


    public enum DefaultFontInfo {
        A('A', 5),
        a('a', 5),
        B('B', 5),
        b('b', 5),
        C('C', 5),
        c('c', 5),
        D('D', 5),
        d('d', 5),
        E('E', 5),
        e('e', 5),
        F('F', 5),
        f('f', 4),
        G('G', 5),
        g('g', 5),
        H('H', 5),
        h('h', 5),
        I('I', 3),
        i('i', 1),
        J('J', 5),
        j('j', 5),
        K('K', 5),
        k('k', 4),
        L('L', 5),
        l('l', 1),
        M('M', 5),
        m('m', 5),
        N('N', 5),
        n('n', 5),
        O('O', 5),
        o('o', 5),
        P('P', 5),
        p('p', 5),
        Q('Q', 5),
        q('q', 5),
        R('R', 5),
        r('r', 5),
        S('S', 5),
        s('s', 5),
        T('T', 5),
        t('t', 4),
        U('U', 5),
        u('u', 5),
        V('V', 5),
        v('v', 5),
        W('W', 5),
        w('w', 5),
        X('X', 5),
        x('x', 5),
        Y('Y', 5),
        y('y', 5),
        Z('Z', 5),
        z('z', 5),
        NUM_1('1', 5),
        NUM_2('2', 5),
        NUM_3('3', 5),
        NUM_4('4', 5),
        NUM_5('5', 5),
        NUM_6('6', 5),
        NUM_7('7', 5),
        NUM_8('8', 5),
        NUM_9('9', 5),
        NUM_0('0', 5),
        EXCLAMATION_POINT('!', 1),
        AT_SYMBOL('@', 6),
        NUM_SIGN('#', 5),
        DOLLAR_SIGN('$', 5),
        PERCENT('%', 5),
        UP_ARROW('^', 5),
        AMPERSAND('&', 5),
        ASTERISK('*', 5),
        LEFT_PARENTHESIS('(', 4),
        RIGHT_PERENTHESIS(')', 4),
        MINUS('-', 5),
        UNDERSCORE('_', 5),
        PLUS_SIGN('+', 5),
        EQUALS_SIGN('=', 5),
        LEFT_CURL_BRACE('{', 4),
        RIGHT_CURL_BRACE('}', 4),
        LEFT_BRACKET('[', 3),
        RIGHT_BRACKET(']', 3),
        COLON(':', 1),
        SEMI_COLON(';', 1),
        DOUBLE_QUOTE('"', 3),
        SINGLE_QUOTE('\'', 1),
        LEFT_ARROW('<', 4),
        RIGHT_ARROW('>', 4),
        QUESTION_MARK('?', 5),
        SLASH('/', 5),
        BACK_SLASH('\\', 5),
        LINE('|', 1),
        TILDE('~', 5),
        TICK('`', 2),
        PERIOD('.', 1),
        COMMA(',', 1),
        SPACE(' ', 3),
        DEFAULT('a', 4);

        private final char character;
        private final int length;

        DefaultFontInfo(char character, int length) {
            this.character = character;
            this.length = length;
        }

        public static DefaultFontInfo forCharacter(char c) {
            for (DefaultFontInfo dFI : DefaultFontInfo.values()) {
                if (dFI.character() == c) {
                    return dFI;
                }
            }
            return DefaultFontInfo.DEFAULT;
        }

        public char character() {
            return this.character;
        }

        public int length() {
            return this.length;
        }

        public int boldLength() {
            if (this == DefaultFontInfo.SPACE) {
                return this.length();
            }
            return this.length + 1;
        }

        public int length(final boolean bold) {
            return bold ? this.boldLength() : this.length();
        }
    }
}
