import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestTextLength {

    @Test
    public void testLongText(){
        String text = "Съешь же ещё этих мягких французских булок, да выпей чаю";
        assertTrue(text.length() > 15, "Текст короче 15 символов");

    }

    @Test
    public void testShortText(){
        String text = "Хорошая погода";
        assertTrue(text.length() > 15, "Текст короче 15 символов");

    }
}
