package hashPackage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class SHA512
{
    String FileName = "src/in.txt";
    String currentLine="";
    String in="";
    byte [] bytes;
    ArrayList<String> blocks = new ArrayList<>();

    private long [] buffers = new long[8];
    private long [] hash = new long[8];

    StringBuilder binary = new StringBuilder();

    BufferedReader br = null;

    public StringBuilder getBinary() {
        return binary;
    }

    FileReader fr = null;

    public void read()
    {
        try
        {
            fr = new FileReader(FileName);
            br = new BufferedReader(fr);

            while((currentLine = br.readLine()) != null)
            {
                in+=currentLine;
                in+="\n";
            }

            bytes = in.getBytes();

            for (byte b : bytes)
            {
                int val = b;
                for (int i = 0; i < 8; i++)
                {
                    binary.append((val & 128) == 0 ? 0 : 1);
                    val <<= 1;
                }
                //binary.append(' ');
            }


        }catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public void addPadding(StringBuilder s)
    {
        int length = s.length();

        int padLength;

        if(length<1024) padLength = 896-length;
        else padLength = (length+128)%1024;

        if(padLength>0)
        {
            s.append('1');

            for(int i=0;i<padLength-1;i++)
            {
                s.append('0');
            }
        }

       // System.out.println(padLength);

        //Appending length bits

        String x = "", y = "";

        int temp = length,a,length1;

        while(temp > 0)
        {
            a = temp%2;
            x = a+""+x;
            temp = temp/2;
        }

        length1 = 128-x.length();

        for(int i=0;i<length1;i++)
        {
            y+="0";
        }

        x = y+x;

        s.append(x);
    }

    public void initializeBuffers()
    {
        buffers[0] = Long.parseUnsignedLong("6A09E667F3BCC908",16);
        buffers[1] = Long.parseUnsignedLong("BB67AE8584CAA73B",16);
        buffers[2] = Long.parseUnsignedLong("3C6EF372FE94F82B",16);
        buffers[3] = Long.parseUnsignedLong("A54FF53A5F1D36F1",16);
        buffers[4] = Long.parseUnsignedLong("510E527FADE682D1",16);
        buffers[5] = Long.parseUnsignedLong("9B05688C2B3E6C1F",16);
        buffers[6] = Long.parseUnsignedLong("1F83D9ABFB41BD6B",16);
        buffers[7] = Long.parseUnsignedLong("5BE0CD19137E2179",16);
    }

    public long  [] f(ArrayList<String> blocks)
    {
        long [] b = new long[8];

        return b;
    }

    public long [] moduloAdd(long [] a,long [] b)
    {
        for(int i=0;i<a.length;i++)
        {
            b[i] = (a[i]+b[i])% (long)Math.pow(2,64);
        }

        return b;
    }

    public void generateOutput()
    {
        hash = buffers;

        for(int i=0;i<blocks.size();i++)
        {
            hash = moduloAdd(hash,f(blocks));
        }


    }

    public void divide()
    {
        //System.out.println(this.binary.toString());
        String temp = "";
        String current = binary.toString();
        for(int i=0;i<current.length();i++)
        {
            temp+=current.charAt(i);

            if((i+1)%1024==0)
            {
                blocks.add(temp);
                temp = "";
            }
        }

        /*for(String s:blocks)
        {
            System.out.println(s);
        }*/
    }

    public void hash()
    {
        read();
        addPadding(this.binary);
        initializeBuffers();
        divide();
    }
}
