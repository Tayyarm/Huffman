package huffman;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;



/**
 * This class contains methods which, when used together, perform the
 * entire Huffman Coding encoding and decoding process
 * 
 * @author Ishaan Ivaturi
 * @author Prince Rawal
 */
public class HuffmanCoding {
    private String fileName;
    private ArrayList<CharFreq> sortedCharFreqList;
    private TreeNode huffmanRoot;
    private String[] encodings;

    /**
     * Constructor used by the driver, sets filename
     * DO NOT EDIT
     * @param f The file we want to encode
     */
    public HuffmanCoding(String f) { 
        fileName = f; 
    }

    /**
     * Reads from filename character by character, and sets sortedCharFreqList
     * to a new ArrayList of CharFreq objects with frequency > 0, sorted by frequency
     */
    public void makeSortedList() {
        StdIn.setFile(fileName);
        ArrayList<CharFreq> list = new ArrayList<CharFreq>();
        int CharCount[]=new int[128];
        double totalChars = 0;
       while(StdIn.hasNextChar())
       { 
            char b = StdIn.readChar();
            CharCount[b]++;
            totalChars++;
       }
        for(int i=0;i<CharCount.length;i++)
        {
            if(CharCount[i]>0)
            {
                char ch = (char)i;
                double prob = CharCount[i]/totalChars;
                CharFreq cf = new CharFreq(ch,prob);   
                list.add(cf);
            }            
        }
        if(list.size()==1)
        {
            CharFreq fake = new CharFreq((char)((list.get(0).getCharacter()+1)%128),0.0);
            list.add(fake);
        }
        Collections.sort(list);
        sortedCharFreqList=list;
	/* Your code goes here */
    }
  
    /**
     * Uses sortedCharFreqList to build a huffman coding tree, and stores its root
     * in huffmanRoot
     */
    public void makeTree() {
    Queue<CharFreq> Source = new Queue<CharFreq>();
    Queue<TreeNode> Target = new Queue<TreeNode>();
    for(int i=0;i<sortedCharFreqList.size();i++)
    {
        Source.enqueue(sortedCharFreqList.get(i));
    }
      if(Source.isEmpty())
      return;
 //     System.out.println("============================number of character="+ Source.size());
    while(!Source.isEmpty())
    {
        TreeNode left = null;
        TreeNode right = null; 
        TreeNode root = null;
        CharFreq rootCF = null;
        CharFreq temp1 =null;
        CharFreq temp2 = null;
        if(Target.isEmpty()) // Works
        {     
            temp1 = Source.dequeue();      
            left = new TreeNode(temp1,null,null);
         /*    if(Source.isEmpty())
            {
                rootCF = new CharFreq(null, temp1.getProbOcc());
                root = new TreeNode(rootCF,left,null);
                Target.enqueue(root);
                break;
            }*/
            temp2 = Source.dequeue();
            right = new TreeNode(temp2, null, null);
            rootCF = new CharFreq(null, temp1.getProbOcc()+temp2.getProbOcc());
            root = new TreeNode(rootCF, left, right);
            Target.enqueue(root);
           // System.out.println("MyFirstTree------------------");     
            //Driver.printTree(root);
            //System.out.println("MyFirstTreeEnd------------------");
        }
        else
        {         
        temp1 = Source.peek();
        TreeNode Ttemp = Target.peek();
        if(temp1.getProbOcc()<=Ttemp.getData().getProbOcc())
        {
            left = new TreeNode(temp1, null, null);
            Source.dequeue();
            if(!Source.isEmpty())
            {
                CharFreq Stemp2 = Source.peek();
                if(Stemp2.getProbOcc()<=Ttemp.getData().getProbOcc())
                {
                    Source.dequeue();
                    right = new TreeNode(Stemp2, null, null);
                    rootCF = new CharFreq(null, temp1.getProbOcc()+Stemp2.getProbOcc());
                }
                else // Target.peek is smaller than Stemp2
                {
                    Target.dequeue();
                    right = Ttemp;
                    rootCF = new CharFreq(null,temp1.getProbOcc()+Ttemp.getData().getProbOcc());     
                }   
                root = new TreeNode(rootCF, left, right);
                Target.enqueue(root);
            }
            else
            {
                Target.dequeue();
                rootCF = new CharFreq(null, temp1.getProbOcc()+Ttemp.getData().getProbOcc());
                root = new TreeNode(rootCF, left, Ttemp);
                Target.enqueue(root);
            }    
        
        }
        else //if(temp1.compareProb(Ttemp.getData())>0) Ttemp is smaller than temp1
        {
            Target.dequeue();
            TreeNode Ttemp2 = null;
            if(!Target.isEmpty())
            {
                Ttemp2 = Target.peek();
            }    
            if(Ttemp2!=null && Ttemp2.getData().getProbOcc()<temp1.getProbOcc())
            {
                Target.dequeue();
                left = Ttemp;
                right = Ttemp2;
                rootCF = new CharFreq(null,Ttemp.getData().getProbOcc()+Ttemp2.getData().getProbOcc());   
            }      
            else if((Ttemp2!=null && Ttemp2.getData().getProbOcc()>=temp1.getProbOcc()) || Target.isEmpty())
            {
                Source.dequeue();
                left = Ttemp;
                right= new TreeNode(temp1,null, null);
                rootCF = new CharFreq(null,Ttemp.getData().getProbOcc()+temp1.getProbOcc());    
            }
            root = new TreeNode(rootCF, left, right);
            Target.enqueue(root);
        }      
       }
    }     
        TreeNode root = null;
        while(Target.size()>1)
        {
            TreeNode temp1 = Target.dequeue();
            TreeNode temp2= Target.dequeue();
            CharFreq rootCF = new CharFreq(null, temp1.getData().getProbOcc()+temp2.getData().getProbOcc());
                root = new TreeNode(rootCF,temp1, temp2);
                Target.enqueue(root);
        }
           huffmanRoot = Target.peek();    
}

    private int getHashIndex(char ch)
    {
        return (int)ch;
    }
    /**
     * Uses huffmanRoot to create a string array of size 128, where each
     * index in the array contains that ASCII character's bitstring encoding. Characters not
     * present in the huffman coding tree should have their spots in the array left null.
     * Set encodings to this array.
     */
    public void makeEncodings() {
        encodings = new String[128]; 
           StringBuilder BitString = new StringBuilder();
           generateHuffmanCode(huffmanRoot, BitString);
	/* Your code goes here */
    }
    private void generateHuffmanCode (TreeNode root, StringBuilder path)
    {
        if(root == null)
            return;
        if(isLeaf(root))
        {
            encodings[getHashIndex(root.getData().getCharacter())]= path.toString();
        }
        
        if(root.getLeft()!=null)
           path.append('0');
        generateHuffmanCode(root.getLeft(), path);
        if(root.getRight()!=null)
            path.append('1');
        generateHuffmanCode(root.getRight(), path);
       int len = path.length();
        if(len>0)
        {
            path.deleteCharAt(len-1);
        }
    }

    /**
     * Using encodings and filename, this method makes use of the writeBitString method
     * to write the final encoding of 1's and 0's to the encoded file.
     * 
     * @param encodedFile The file name into which the text file is to be encoded
     */
    public void encode(String encodedFile) {
        StdIn.setFile(fileName);
        String bits = "";
      while(StdIn.hasNextChar())
      {
        char c = StdIn.readChar();
        //System.out.println("c="+c+", bits=" + encodings[c]);
        bits=bits.concat(encodings[c]);
      }
      //System.out.println("encode.bits="+ bits);
      writeBitString(encodedFile,bits);

	/* Your code goes here */
    }
    
    /**
     * Writes a given string of 1's and 0's to the given file byte by byte
     * and NOT as characters of 1 and 0 which take up 8 bits each
     * DO NOT EDIT
     * 
     * @param filename The file to write to (doesn't need to exist yet)
     * @param bitString The string of 1's and 0's to write to the file in bits
     */
    public static void writeBitString(String filename, String bitString) {
        byte[] bytes = new byte[bitString.length() / 8 + 1];
        int bytesIndex = 0, byteIndex = 0, currentByte = 0;

        // Pad the string with initial zeroes and then a one in order to bring
        // its length to a multiple of 8. When reading, the 1 signifies the
        // end of padding.
        int padding = 8 - (bitString.length() % 8);
        String pad = "";
        for (int i = 0; i < padding-1; i++) pad = pad + "0";
        pad = pad + "1";
        bitString = pad + bitString;

        // For every bit, add it to the right spot in the corresponding byte,
        // and store bytes in the array when finished
        for (char c : bitString.toCharArray()) {
            if (c != '1' && c != '0') {
                System.out.println("Invalid characters in bitstring");
                return;
            }

            if (c == '1') currentByte += 1 << (7-byteIndex);
            byteIndex++;
            
            if (byteIndex == 8) {
                bytes[bytesIndex] = (byte) currentByte;
                bytesIndex++;
                currentByte = 0;
                byteIndex = 0;
            }
        }
        
        // Write the array of bytes to the provided file
        try {
            FileOutputStream out = new FileOutputStream(filename);
            out.write(bytes);
            out.close();
        }
        catch(Exception e) {
            System.err.println("Error when writing to file!");
        }
    }

    /**
     * Using a given encoded file name, this method makes use of the readBitString method 
     * to convert the file into a bit string, then decodes the bit string using the 
     * tree, and writes it to a decoded file. 
     * 
     * @param encodedFile The file which has already been encoded by encode()
     * @param decodedFile The name of the new file we want to decode into
     */
    public void decode(String encodedFile, String decodedFile) {
        StdOut.setFile(decodedFile);
         String bits = readBitString(encodedFile);
         TreeNode ptr = huffmanRoot;
         StringBuilder sb = new StringBuilder();
        for(int i=0; i<bits.length();i++)
        {
           char bit = bits.charAt(i);   
           if(bit=='0')
              ptr=ptr.getLeft();
           else
             ptr=ptr.getRight();

           if(isLeaf(ptr))
           {
             sb.append(ptr.getData().getCharacter()); 
             ptr=huffmanRoot;
           }  
        }
        StdOut.print( sb.toString());
	/* Your code goes here */
    }
    private boolean isLeaf(TreeNode ptr)
    {
        boolean a = false;
        if(ptr!=null && ptr.getLeft()==null && ptr.getRight()==null)
        {
            a=true;
        }      
        return a; 
    }
    /**
     * Reads a given file byte by byte, and returns a string of 1's and 0's
     * representing the bits in the file
     * DO NOT EDIT
     * 
     * @param filename The encoded file to read from
     * @return String of 1's and 0's representing the bits in the file
     */
    public static String readBitString(String filename) {
        String bitString = "";
        
        try {
            FileInputStream in = new FileInputStream(filename);
            File file = new File(filename);
            byte bytes[] = new byte[(int) file.length()];
            in.read(bytes);
            in.close();
            
            // For each byte read, convert it to a binary string of length 8 and add it
            // to the bit string
            for (byte b : bytes) {
                bitString = bitString + 
                String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
            }

            // Detect the first 1 signifying the end of padding, then remove the first few
            // characters, including the 1
            for (int i = 0; i < 8; i++) {
                if (bitString.charAt(i) == '1') return bitString.substring(i+1);
            }
            
            return bitString.substring(8);
        }
        catch(Exception e) {
            System.out.println("Error while reading file!");
            return "";
        }
    }

    /*
     * Getters used by the driver. 
     * DO NOT EDIT or REMOVE
     */

    public String getFileName() { 
        return fileName; 
    }

    public ArrayList<CharFreq> getSortedCharFreqList() { 
        return sortedCharFreqList; 
    }

    public TreeNode getHuffmanRoot() { 
        return huffmanRoot; 
    }

    public String[] getEncodings() { 
        return encodings; 
    }
}
