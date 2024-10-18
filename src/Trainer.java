import java.util.ArrayList;

public class Trainer {
    public static void trainPerceptron(Perceptron perceptron, ArrayList<Item> list){
        perceptron.learn(list);
    }
}
