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
        return (e&f)^ (~(e)&g);
    }

    public long maj(long a, long b,long c)
    {
        return (a&b) ^ (a&c) ^ (b&c);
    }

    public long sigma0(long word)
    {
        return Long.rotateRight(word,1) ^ Long.rotateRight(word,8) ^ word >> 7;
    }

    public long sigma1(long word)
    {
        return Long.rotateRight(word,19) ^ Long.rotateRight(word,61) ^ word >> 6;
    }

    public long modAdd(long a,long b)
    {
        return (a+b)%(long) Math.pow(2,64);
    }

    public long  [] f(String block)
    {
        long t1,t2,a,b,c,d,e,f,g,h;

        final String [] keyInHex = {"428a2f98d728ae22", "7137449123ef65cd", "b5c0fbcfec4d3b2f", "e9b5dba58189dbbc",
                "3956c25bf348b538", "59f111f1b605d019", "923f82a4af194f9b", "ab1c5ed5da6d8118", "d807aa98a3030242",
                "12835b0145706fbe", "243185be4ee4b28c", "550c7dc3d5ffb4e2", "72be5d74f27b896f", "80deb1fe3b1696b1",
                "9bdc06a725c71235", "c19bf174cf692694", "e49b69c19ef14ad2", "efbe4786384f25e3", "0fc19dc68b8cd5b5",
                "240ca1cc77ac9c65", "2de92c6f592b0275", "4a7484aa6ea6e483", "5cb0a9dcbd41fbd4", "76f988da831153b5",
                "983e5152ee66dfab", "a831c66d2db43210", "b00327c898fb213f", "bf597fc7beef0ee4", "c6e00bf33da88fc2",
                "d5a79147930aa725", "06ca6351e003826f", "142929670a0e6e70", "27b70a8546d22ffc", "2e1b21385c26c926",
                "4d2c6dfc5ac42aed", "53380d139d95b3df", "650a73548baf63de", "766a0abb3c77b2a8", "81c2c92e47edaee6",
                "92722c851482353b", "a2bfe8a14cf10364", "a81a664bbc423001", "c24b8b70d0f89791", "c76c51a30654be30",
                "d192e819d6ef5218", "d69906245565a910", "f40e35855771202a", "106aa07032bbd1b8", "19a4c116b8d2d0c8",
                "1e376c085141ab53", "2748774cdf8eeb99", "34b0bcb5e19b48a8", "391c0cb3c5c95a63", "4ed8aa4ae3418acb",
                "5b9cca4f7763e373", "682e6ff3d6b2b8a3", "748f82ee5defb2fc", "78a5636f43172f60", "84c87814a1f0ab72",
                "8cc702081a6439ec", "90befffa23631e28", "a4506cebde82bde9", "bef9a3f7b2c67915", "c67178f2e372532b",
                "ca273eceea26619c", "d186b8c721c0c207", "eada7dd6cde0eb1e", "f57d4f7fee6ed178", "06f067aa72176fba",
                "0a637dc5a2c898a6", "113f9804bef90dae", "1b710b35131c471b", "28db77f523047d84", "32caab7b40c72493",
                "3c9ebe0a15c9bebc", "431d67c49c100d4c", "4cc5d4becb3e42b6", "597f299cfc657e2a", "5fcb6fab3ad6faec",
                "6c44198c4a475817"};

        long [] k = new long[80];


        for(int i=0;i<80;i++)
        {
            k[i] = Long.parseUnsignedLong(keyInHex[i],16);
        }


        long [] words = new long[80];

        for(int i=0;i<16;i++)
        {
            words[i] = Long.parseUnsignedLong(block.substring(i*64,(i+1)*64-1),2);
        }

        for(int t=16;t<80;t++)
        {
            words[t] = modAdd(modAdd(sigma1(words[t-2]), words[t-7] ),modAdd( sigma0(words[t-15]) , words[t-16]));
        }



        for(int t=0;t<80;t++)
        {
            a = buffers[0];
            b = buffers[1];
            c = buffers[2];
            d = buffers[3];
            e = buffers[4];
            f = buffers[5];
            g = buffers[6];
            h = buffers[7];

            t1 = modAdd(modAdd(modAdd(h,ch(e,f,g)),modAdd(Sigma1(buffers[4]),words[t])),k[t]);
            t2 = modAdd(Sigma0(a),maj(a,b,c));

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

        for(int i=0;i<hash.length;i++)
        {
            System.out.print(Long.toHexString(hash[i]));
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
        generateOutput();
    }
}
