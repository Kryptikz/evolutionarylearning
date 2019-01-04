import java.util.*;
public class BitcoinThread extends Thread {
    ArrayList<Double> prices;
    final int NORMALIZEPOINT = 25000;    
    Network n;
    long totalscore;
    public BitcoinThread(Network net) {
        prices=FileDataReader.btcHour();
        n=net;
    }
    public BitcoinThread() {
        prices=FileDataReader.btcHour();
    }
    public void run() {
        //for each set of 48, 
        totalscore=0;
        for(int i=0;i<prices.size()-73;i++) {
            double[] netin = new double[48];
            for(int i2=i;i2<i+48;i2++) {
                netin[i2-i]=(prices.get(i2))/NORMALIZEPOINT;
            }
            double[] netout = n.forward(netin);
            //de-normalize output of normalized data
            for(int v=0;v<24;v++) {
                double dis = Math.abs(prices.get(v+i+48)-(NORMALIZEPOINT*netout[v]));
                if (dis>=2500) {
                    totalscore-=25;
                } else if (dis<=10) {
                    totalscore+=1000;
                } else if (dis<=100) {
                    totalscore+=100;
                } else if (dis<=250) {
                    totalscore+=50;
                } else if (dis<=500) {
                    totalscore+=20;
                } else if (dis<=1000) {
                    totalscore+=5;
                }            
                //System.out.println("distance was: " + dis + " - guess was: " + (NORMALIZEPOINT*(netout[v])) + " - correct answer was: " + (prices.get(v+i+48)));
            }
        }
        //System.out.println("network achieved score of: " + totalscore);
    }
    public int getScore() {
        return (int)totalscore;
    }
    public void setNet(Network net) {
        n=net;
    }
    public Network getNet() {
        return n;
    }
}