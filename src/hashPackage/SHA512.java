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


    public long Sigma0(long a)
    {
        return Long.rotateRight(a,28) ^ Long.rotateRight(a,34) ^ Long.rotateRight(a,39);
    }

    public long Sigma1(long e)
    {
        return Long.rotateRight(e,14) ^ Long.rotateRight(e,18) ^ Long.rotateRight(e,41);
    }

    public long ch(long e,long f,long g)
    {
        return (e&f)^ (Long.reverse(e)&g);
    }

    public long maj(long a, long b,long c)
    {
        return (a&b) ^ (a&c) ^ (b&c);
    }

    public long sigma0(long word)
    {
        return Long.rotateRight(word,1) ^ Long.rotateRight(word,8) ^ word << 7;
    }

    public long sigma1(long word)
    {
        return Long.rotateRight(word,19) ^ Long.rotateRight(word,61) ^ word << 6;
    }

    public long modAdd(long a,long b)
    {
        return (a+b)%(long) Math.pow(2,64);
    }

    public long  [] f(String block)
    {
        long t1,t2,a,b,c,d,e,f,g,h;

        long [] k = new long[80];


        long [] words = new long[80];

        for(int i=0;i<16;i++)
        {
            words[i] = Long.parseUnsignedLong(block.substring(i,i*64));
        }

        for(int t=17;t<80;t++)
        {
            words[t] = sigma1(words[t-2]) ^ words[t-7] ^ sigma0(words[t-15]) ^ words[t-16];
        }

        a = buffers[0];
        b = buffers[1];
        c = buffers[2];
        d = buffers[3];
        e = buffers[4];
        f = buffers[5];
        g = buffers[6];
        h = buffers[7];

        for(int t=0;t<80;t++)
        {
            t1 = modAdd(modAdd(modAdd(buffers[7],ch(buffers[4],buffers[5],buffers[6])),modAdd(Sigma1(buffers[4]),words[t])),k[t]);
            t2 = modAdd(Sigma0(buffers[0]),maj(buffers[0],buffers[1],buffers[2]));

            buffers[0] = modAdd(t1,t2);
            buffers[1] = a;
            buffers[2] = b;
            buffers[3] = c;
            buffers[4] = modAdd(d,t1);
            buffers[5] = e;
            buffers[6] = f;
            buffers[7] = g;
        }

        return buffers;
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
            hash = moduloAdd(hash,f(blocks.get(i)));
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
