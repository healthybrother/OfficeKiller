import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.*;

import java.io.File;


/**
 * @author healthyin
 * @version Test 2022/12/3
 */
public class Test {
    public static void main(String[] args) throws Docx4JException {
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
        //创建docx4j工厂
        ObjectFactory factory = Context.getWmlObjectFactory();
        //获得word包中document.xml文件内容
        MainDocumentPart main = wordMLPackage.getMainDocumentPart();
        //获得document.xml文件下body标签内容
        Body body = main.getContents().getBody();
        //创建段落标签
        P p = factory.createP();
        //创建段落格式标签
        PPr pPr = factory.createPPr();
        //创建段落样式标签
        PPrBase.PStyle ps = new PPrBase.PStyle();
        //设置段落样式标签的val属性值为前面创建的样式id
        ps.setVal("test");
        //将设置好的段落样式标签设置到段落格式标签中
        pPr.setPStyle(ps);
        //将设置好的段落格式标签设置到段落标签中
        p.setPPr(pPr);
        //创建r标签
        R run = factory.createR();
        //创建t标签
        Text t = factory.createText();
        //设置t标签内的内容
        t.setValue("测试内容");
        //将设置好的t标签设置到r标签中
        run.getContent().add(t);
        //将设置好的r标签设置到段落标签中
        p.getContent().add(run);
        //将设置好的段落标签加入body标签中
        body.getContent().add(p);
        //设置word文档要存放的文件
        File file = new File("src/main/resources/test.docx");
        //将设置好的word包保存到指定文件中
        wordMLPackage.save(file);

    }
}
