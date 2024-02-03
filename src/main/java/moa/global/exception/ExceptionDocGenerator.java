package moa.global.exception;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.http.HttpStatus;

public class ExceptionDocGenerator {

    public static void main(String[] args) throws Exception {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage("moa"))
                .setScanners(new SubTypesScanner(false)));
        Set<Class<? extends MoaExceptionType>> subTypesOf = reflections.getSubTypesOf(MoaExceptionType.class);
        // resources의 static 폴더 하위에 생성되게 해줘
        Path outputPath = Paths.get("src", "main", "resources", "static", "exception_doc.html");
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(outputPath))) {
            writer.println("""
                    <html>
                        <head>
                            <meta charset='UTF-8'>
                            <link rel="stylesheet" type="text/css" href="./styles.css">
                        </head>
                    <body>
                    """);
            for (Class<? extends MoaExceptionType> aClass : subTypesOf) {
                writer.println("<h2>" + aClass.getSimpleName() + "</h2>");
                writer.println("""
                        <table>
                            <tr>
                                <th>HttpStatus</th>
                                <th>Name</th>
                                <th>Message</th>
                            </tr>
                        """);

                for (Object enumConstant : aClass.getEnumConstants()) {
                    if (enumConstant instanceof MoaExceptionType) {
                        MoaExceptionType exceptionType = (MoaExceptionType) enumConstant;
                        HttpStatus httpStatus = exceptionType.getHttpStatus();
                        String message = exceptionType.getMessage();
                        String name = exceptionType.name();
                        writer.println("""
                                <tr>
                                    <td> %s </td>
                                    <td> %s </td>
                                    <td> %s </td>
                                </tr>
                                """
                                .formatted(httpStatus.value(), name, message)
                        );
                    }
                }
                writer.println("</table>");
            }
            writer.println("</body></html>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
