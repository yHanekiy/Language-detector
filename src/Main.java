import org.w3c.dom.ls.LSOutput;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static HashMap<String, Integer> language = new HashMap<>();
    public static ArrayList<Item> testList = new ArrayList<>();
    public static void main(String[] args) {
        boolean isFinished = false;
        Scanner scanner = new Scanner(System.in);
        String option;
        List<Perceptron> perceptrons = new ArrayList<>();
        while (!isFinished) {
            System.out.print("Select of the option" +
                    "\n1)Split data for training and test" +
                    "\n2)Change test data" +
                    "\n3)Train" +
                    "\n4)Check the type of items" +
                    "\n5)Enter your own item" +
                    "\n6)Finish" +
                    "\nOption: ");
            option = scanner.nextLine();
            if (option.equals("1")){
                splitData(Paths.get("C:\\Users\\Yura\\Desktop\\ManyLayer\\src\\Models"),80);
            } else if (option.equals("2")) {
                testList.clear();
                System.out.println("Write path: ");
                try {
                    List<String> list = Files.readAllLines(Paths.get(scanner.nextLine()));
                    for (String str : list){
                        String[] tab = str.split("\\|\\$\\|");
                        Item item = getOnlyOne(tab[0]);
                        item.specie=tab[1].trim();
                        testList.add(item);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else if (option.equals("3")) {
                ArrayList<Item> items;
                ArrayList<Item> result = new ArrayList<>();
                perceptrons.clear();
                int counter = 0;
                for (File f : new File("C:\\Users\\Yura\\Desktop\\ManyLayer\\src\\Models").listFiles()) {
                    perceptrons.add(new Perceptron(f.getName(), 26));
                    for (File f1 : new File("C:\\Users\\Yura\\Desktop\\ManyLayer\\src\\Models").listFiles()) {
                        items = fromFileToVector(Paths.get(f1.getPath()), perceptrons.get(counter));
                        for (Item i : items) {
                            result.add(i);
                        }
                    }
                    Collections.shuffle(result);
                    Trainer.trainPerceptron(perceptrons.get(counter), result);
                    counter++;
                }
            } else if (option.equals("4")) {
                if (!testList.isEmpty()) {
                    ArrayList<Integer> err = new ArrayList<>();
                    int count = 1;
                    HashMap<String, Double> map;
                    Collections.shuffle(testList);
                    for (Item item : testList) {
                        map = new HashMap<>();
                        for (Perceptron perceptron : perceptrons) {
                            map.put(perceptron.name, perceptron.compute(item.parameters));
                        }
                        List<Map.Entry<String, Double>> entryList = new ArrayList<>(map.entrySet());

                        Collections.sort(entryList, Map.Entry.comparingByValue());

                        Map<String, Double> sortedMap = new LinkedHashMap<>();
                        for (Map.Entry<String, Double> entry : entryList) {
                            sortedMap.put(entry.getKey(), entry.getValue());
                        }
                        System.out.println(count + ") Real: " + item.specie + " | Compute: " + sortedMap.keySet().toArray()[sortedMap.keySet().toArray().length - 1]);
                        if (!item.specie.equals(sortedMap.keySet().toArray()[sortedMap.keySet().toArray().length - 1])) {
                            err.add(count);
                        }
                        count++;
                    }
                    for (Integer i : err){
                        System.out.println(i);
                    }
                    if (err.isEmpty()) {
                        System.out.println("No one");
                    } else {
                        System.out.println("Errors: "+(double)err.size()/testList.size()*100+"%");
                    }
                }
            } else if (option.equals("5")) {
                System.out.print("Write the sentence: ");
                Item item = getOnlyOne(scanner.nextLine());
                String answer =computePerceptrons(perceptrons, item);
                System.out.println("Compute: "+answer);
            } else if (option.equals("6")) {
                isFinished=true;
            }else {
                System.out.println("We don`t have this option");
            }
            System.out.println("----------------------------------");
        }
    }
    public static void initializeMap(){
        for (int i=0; i<26;i++){
            char c = (char)('a'+i);
            language.put(String.valueOf(c),0);
        }
    }
    public static String computePerceptrons(List<Perceptron> perceptrons, Item item){
        HashMap<String, Double> map = new HashMap<>();
        for (Perceptron perceptron : perceptrons) {
            map.put(perceptron.name, perceptron.compute(item.parameters));
        }
        List<Map.Entry<String, Double>> entryList = new ArrayList<>(map.entrySet());

        Collections.sort(entryList, Map.Entry.comparingByValue());

        Map<String, Double> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Double> entry : entryList) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return (String) sortedMap.keySet().toArray()[sortedMap.keySet().toArray().length - 1];
    }
    public static ArrayList<Item> fromFileToVector(Path path, Perceptron perceptron){
        try {
            ArrayList<Item> items = new ArrayList<>();
            List<String> list = Files.readAllLines(path).stream().map(x->x.toLowerCase()).toList();
            Map<Character, Integer> alphabetMap = new HashMap<>();
            char letter = 'a';
            for (int i = 0; i <= 25; i++) {
                alphabetMap.put(letter, i);
                letter++;
            }
            for (String str : list){
                int sum = str.length();
                language.clear();
                initializeMap();
                for (char c : str.toCharArray()){
                    if (alphabetMap.containsKey(c)){
                        language.put(String.valueOf(c), language.getOrDefault(String.valueOf(c),0)+1);
                    }
                }
                ArrayList<Double> temp = new ArrayList<>();
                for (Map.Entry<String, Integer> entry : language.entrySet()) {
                    temp.add((double)entry.getValue()/sum);
                }
                if (perceptron.name.equals(path.getFileName().toString())) {
                    items.add(new Item(temp, path.getFileName().toString(), 1));
                }else{
                    items.add(new Item(temp, path.getFileName().toString(), 0));
                }
            }
            return items;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static Item getOnlyOne(String str){
        str = str.toLowerCase();
        Map<Character, Integer> alphabetMap = new HashMap<>();
        char letter = 'a';
        for (int i = 0; i <= 25; i++) {
            alphabetMap.put(letter, i);
            letter++;
        }
        int sum = str.length();
        language.clear();
        initializeMap();
        for (char c : str.toCharArray()){
            if (alphabetMap.containsKey(c)){
                language.put(String.valueOf(c), language.getOrDefault(String.valueOf(c),0)+1);
            }
        }
        ArrayList<Double> temp = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : language.entrySet()) {
            temp.add((double)entry.getValue()/sum);
        }
        return new Item(temp,"Cos",10);
    }

    public static void splitData(Path directory, int percent) {
        try {
            for (File f : new File("C:\\Users\\Yura\\Desktop\\ManyLayer\\src\\Trains").listFiles()) {
                f.delete();
            }

            for (File f : new File(directory.toString()).listFiles()) {
                File fq = new File("C:\\Users\\Yura\\Desktop\\ManyLayer\\src\\"+f.getName());
                int counter =0;
                FileWriter trainWriter =  new FileWriter("C:\\Users\\Yura\\Desktop\\ManyLayer\\src\\Trains\\"+f.getName());
                FileWriter testWriter =  new FileWriter("C:\\Users\\Yura\\Desktop\\ManyLayer\\src\\"+f.getName());

                List<String> list = Files.readAllLines(Paths.get(f.getPath()));
                for (String str: list){
                    if (counter<list.size()*percent/100){
                        trainWriter.write(str+"\n");
                    }else{
                        testWriter.write(str+"\n");
                    }
                    counter++;
                }
                testWriter.close();
                trainWriter.close();
                testList.addAll(fromFileToVector(Paths.get(fq.getPath()),new Perceptron("New",26)));
                fq.delete();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
