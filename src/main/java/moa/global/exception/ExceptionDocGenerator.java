package moa.global.exception;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;

public class ExceptionDocGenerator {

    public static void main(String[] args) {
        writeDocs(getExceptionTypes());
    }

    private static List<Class<?>> getExceptionTypes() {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        File moa = new File(classLoader.getResource("moa").getFile());
        List<Class<?>> classes = new ArrayList<>();
        readClass(moa.getAbsolutePath(), moa.listFiles(), classes);
        return classes.stream()
                .filter(MoaExceptionType.class::isAssignableFrom)
                .filter(it -> !it.isInterface())
                .toList();
    }

    private static void readClass(String rootPath, File[] files, List<Class<?>> store) {
        for (File file : files) {
            if (file.isDirectory()) {
                readClass(rootPath, file.listFiles(), store);
            } else {
                String replace = file.getAbsolutePath()
                        .replace(rootPath, "")
                        .replace("/", ".")
                        .replace(".class", "")
                        .replaceFirst("\\.", "moa.");
                try {
                    store.add(Class.forName(replace));
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static void writeDocs(List<Class<?>> subTypesOf) {
        Path outputPath = Paths.get("src", "main", "resources", "static", "exception_doc.html");
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(outputPath))) {
            writer.println("""
                    <html>
                        <head>
                            <meta charset='UTF-8'>
                            <link rel="stylesheet" type="text/css" href="./styles.css">
                            <!-- tocbot -->
                            	  <script src="https://cdnjs.cloudflare.com/ajax/libs/tocbot/4.11.1/tocbot.min.js">	</script>
                            	  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/tocbot/4.11.1/tocbot.css">
                            <!-- tocbot -->
                            <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
                        </head>
                    <body>
                    """);
            for (Class<?> aClass : subTypesOf) {
                writer.println("<div class=\"toc toc-fixed\" ></div>");
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
                    if (enumConstant instanceof MoaExceptionType exceptionType) {
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
            writer.println("""
                                 <!-- tocbot-->	
                                	<script>
                                		var content = document.querySelector('body')
                                		var headings = content.querySelectorAll('h1, h2, h3, h4, h5, h6, h7')
                                		var headingMap = {}
                                        
                                		Array.prototype.forEach.call(headings, function (heading) {
                                				var id = heading.id ? heading.id : heading.textContent.trim().toLowerCase()
                                									 .split(' ').join('-').replace(/[\\!\\@\\#\\$\\%\\^\\&\\*\\(\\):]/ig, '')
                                				headingMap[id] = !isNaN(headingMap[id]) ? ++headingMap[id] : 0
                                				if (headingMap[id]) {
                                					heading.id = id + '-' + headingMap[id]
                                				} else {
                                					heading.id = id
                                				}
                                			})
                                        
                                		tocbot.init({
                                			tocSelector: '.toc',
                                			contentSelector: 'body',
                                			headingSelector:'h1, h2, h3',
                                			hasInnerContainers: false
                                		});
                                       \s
                                		$(document).ready(function(){
                                			$('.toc').addClass('toc-absolute');
                                        
                                			var toc_top = $('.toc').offset().top - 165;
                                			$(window).scroll(function() {
                                				if ($(this).scrollTop() >= toc_top) {
                                					$('.toc').addClass('toc-fixed');
                                					$('.toc').removeClass('toc-absolute');
                                				} else {
                                					$('.toc').addClass('toc-absolute');
                                					$('.toc').removeClass('toc-fixed');
                                				}
                                			});
                                		});
                                	</script>
                                	<!-- tocbot-->
                    </body></html>
                    """);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
