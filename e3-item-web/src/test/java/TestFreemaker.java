import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * @author quan
 * @create 2020-05-10 23:48
 */
public class TestFreemaker {
    @Test
    public void testFreemaker() throws IOException, TemplateException {
        Configuration configuration = new Configuration(Configuration.getVersion());
        configuration.setDirectoryForTemplateLoading(new File(""));
        configuration.setDefaultEncoding("utf-8");
        Template template = configuration.getTemplate("hello.ftl");
        Map data = new HashMap();
        data.put("hello", "hello-freemaker");
        Writer out = new FileWriter("");
        template.process(data, out);
        out.close();

    }
}
