package keystone;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class JobDispatcher {
    public List<String> getNewItems() {
        List<String> itemsToParse = new ArrayList<>();
       // itemsToParse.add("https://wwwsc.ekeystone.com/Search/Detail?pid=BLS24-253208");
     //   itemsToParse.add("https://wwwsc.ekeystone.com/Search/Detail?pid=SKYH7006");
        itemsToParse.add("https://wwwsc.ekeystone.com/Search/Detail?pid=BLS47-244566");
        itemsToParse.add("https://wwwsc.ekeystone.com/Search/Detail?pid=BLS33-186009");

       // return itemsToParse;

        return badGetLinks();
    }

    List<String> badGetLinks(){
        List<String> parts = new ArrayList<>();
        Scanner s = null;
        try {
            s = new Scanner(new File("src\\main\\resources\\links.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while (s.hasNext()){
            parts.add(s.next());
        }
        s.close();
        List<String> links = new ArrayList<>();
        parts.forEach(System.out::println);
        parts.forEach(part->{
            String link = "https://wwwsc.ekeystone.com/Search/Detail?pid=BLS" + part;
            links.add(link);
        });

        return links;
    }
}
