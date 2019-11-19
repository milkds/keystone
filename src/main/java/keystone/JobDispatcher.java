package keystone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class JobDispatcher {
    private static final Logger logger = LogManager.getLogger(JobDispatcher.class.getName());

    public Set<String> getNewItems() {
       /* List<String> itemsToParse = new ArrayList<>();
       // itemsToParse.add("https://wwwsc.ekeystone.com/Search/Detail?pid=BLS24-253208");
     //   itemsToParse.add("https://wwwsc.ekeystone.com/Search/Detail?pid=SKYH7006");
        itemsToParse.add("https://wwwsc.ekeystone.com/Search/Detail?pid=BLS47-244566");
        itemsToParse.add("https://wwwsc.ekeystone.com/Search/Detail?pid=BLS33-186009");*/

       // return itemsToParse;

     //   List<String> itemsToParse = badGetLinks();
        List<String> itemsToParse = getFullLinks();
        logger.debug("Total items to parse in file " + itemsToParse.size());
        //Set<String> parsedItems = KeyDAO.getParsedItems();
        //Set<String> parsedItemsLinks = getParsedItemsLinks(parsedItems);
        Set<String> parsedItemsLinks = KeyDAO.getParsedItemLinks();
        logger.debug("Parsed items total " + parsedItemsLinks.size());
        Set<String> itemsToParseSet = new HashSet<>();
        itemsToParse.forEach(item->{
            if (!parsedItemsLinks.contains(item)){
                itemsToParseSet.add(item);
            }
        });
        logger.debug("Total items to parse for current launch " + itemsToParseSet.size());

        return itemsToParseSet;
    }

    public List<String> getFullLinks() {
        List<String> links = new ArrayList<>();
        Scanner s = null;
        try {
            s = new Scanner(new File("src\\main\\resources\\links.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while (s.hasNext()){
            links.add(s.next());
        }
        s.close();

        return links;
    }

    private Set<String> getParsedItemsLinks(Set<String> parsedItems) {
        Set<String> result = new HashSet<>();
        parsedItems.forEach(item->{
            result.add("https://wwwsc.ekeystone.com/Search/Detail?pid="+item);
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
