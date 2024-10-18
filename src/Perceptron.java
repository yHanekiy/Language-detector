import javax.xml.stream.events.StartDocument;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class Perceptron {

    public double[] weights;
    public int iterations;
    public double learningRate,localError, bias;
    public String name;
    public Perceptron(String name, int inputs){
        this.name=name;
        Random random = new Random();
        weights = new double[inputs];
        for (int i=0; i<weights.length;i++){
            weights[i]= random.nextDouble();
        }
        this.bias=0;
        this.iterations=100;
        this.learningRate=0.1;
    }
    public double compute(ArrayList<Double> inputs){
        double suma=0;
        for (int i=0; i<inputs.size();i++){
            suma+=inputs.get(i)*weights[i];
        }
        return signum(suma);
    }
    public double signum(double suma){
        return (2/(1+Math.pow(Math.E,(suma*-1))))-1;
    }
    public void learn(ArrayList<Item> list){
        for (int i =0; i<iterations;i++){
            for (Item item : list){
                double output = compute(item.parameters);
                localError = item.indexClass - output;
                for (int j =0; j<weights.length;j++){
                    weights[j]+= learningRate * localError * item.parameters.get(j);
                }
                bias+=learningRate*localError;
            }
        }

    }
}
