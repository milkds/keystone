package keystone;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class JobDispatcher {
    public Set<String> getNewItems() {
       /* List<String> itemsToParse = new ArrayList<>();
       // itemsToParse.add("https://wwwsc.ekeystone.com/Search/Detail?pid=BLS24-253208");
     //   itemsToParse.add("https://wwwsc.ekeystone.com/Search/Detail?pid=SKYH7006");
        itemsToParse.add("https://wwwsc.ekeystone.com/Search/Detail?pid=BLS47-244566");
        itemsToParse.add("https://wwwsc.ekeystone.com/Search/Detail?pid=BLS33-186009");*/

       // return itemsToParse;

        List<String> itemsToParse = badGetLinks();
        Set<String> parsedItems = KeyDAO.getParsedItems();
        Set<String> parsedItemsLinks = getParsedItemsLinks(parsedItems);
        Set<String> itemsToParseSet = new HashSet<>();
        itemsToParse.forEach(item->{
            if (!parsedItemsLinks.contains(item)){
                itemsToParseSet.add(item);
            }
        });

        return itemsToParseSet;
    }

    private Set<String> getParsedItemsLinks(Set<String> parsedItems) {
        Set<String> result = new HashSet<>();
        parsedItems.forEach(item->{
            result.add("https://wwwsc.ekeystone.com/Search/Detail?pid=BLS"+item);
        });

        return result;
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
