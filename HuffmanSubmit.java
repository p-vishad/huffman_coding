import java.util.HashMap;
import java.util.Stack;

public class HuffmanSubmit implements Huffman{

    public void encode(String inputFile, String outputFile, String freqFile){

        create_freq(inputFile, freqFile);

        BinaryIn in = new BinaryIn(inputFile);
        BinaryOut out = new BinaryOut(outputFile);

        HashMap<Byte, String> encodeMap = get_encodeMap(get_HuffmanTree(freqFile));

        String buffer;
        while (!in.isEmpty()){
            buffer = encodeMap.get(in.readByte());
            for (int i = 0; i < buffer.length(); i++){
                if (buffer.charAt(i)=='1'){
                    out.write(true);
                }else{
                    out.write(false);
                }
            }
        }
        out.flush();

    }

    public void decode(String inputFile, String outputFile, String freqFile){

        BinaryIn in = new BinaryIn(inputFile);
        BinaryOut out = new BinaryOut(outputFile);

        HashMap<String, Byte> decodeMap = get_decodeMap(get_HuffmanTree(freqFile));

        String buffer="";
        while (!in.isEmpty()){
            if (in.readBoolean()==true){
                buffer += "1";
            }else {
                buffer += "0";
            }
            if (decodeMap.containsKey(buffer)){
                out.write(decodeMap.get(buffer));
                buffer = "";
            }
        }
        out.flush();

    }

    public void create_freq(String inputFile, String freqFile){

        BinaryIn in = new BinaryIn(inputFile);
        BinaryOut freq = new BinaryOut(freqFile);

        HashMap<Byte, Integer> frequency = new HashMap<>();

        Byte buffer;

        //store byte and corresponding frequency to hash map
        while (!in.isEmpty()){
            buffer = in.readByte();
            if (frequency.containsKey(buffer)){
                frequency.replace(buffer, frequency.get(buffer)+1);
            }else{
                frequency.put(buffer, 1);
            }
        }

        //write to file from hash map
        for (HashMap.Entry<Byte, Integer> entry : frequency.entrySet()) {
            freq.write(entry.getKey() + ":" + entry.getValue() + "\n");
        }
        freq.flush();

    }

    public Node get_HuffmanTree(String freqFile){

        BinaryIn freq = new BinaryIn(freqFile);

        String raw_data = freq.readString();
        String data[] = raw_data.split("\n");

        Node[] nodes = new Node[data.length];

        for (int i = 0; i<data.length; i++){
            Node tempNode = new Node();
            tempNode.frequency = Integer.parseInt(data[i].split(":")[1]);
            tempNode.character = Byte.valueOf(data[i].split(":")[0]);
            nodes[i] = tempNode;
        }

        //build the huffman tree
        while (nodes_size(nodes)>1){
            sort_nodes(nodes);
            int size = nodes_size(nodes);
            Node temp1 = nodes[size-1];
            Node temp2 = nodes[size-2];
            Node temp3 = new Node();
            temp3.left = temp1;
            temp3.right = temp2;
            temp3.frequency = temp1.frequency + temp2.frequency;
            temp1.parent = temp3;
            temp2.parent = temp3;
            nodes[size-1] = null;
            nodes[size-2] = temp3;
        }

        return nodes[0];

    }

    public HashMap<Byte, String> get_encodeMap(Node node){
        HashMap<Byte, String> encodeMap = new HashMap<>();
        Stack<Node> nodes = new Stack<>();
        traversal(node, nodes);
        while (!nodes.empty()){
            Node current_node = nodes.pop();
            Node child_node = current_node;
            Node parent_node;
            String prefix_code = "";
            String reversed = "";
            while (child_node.parent!=null){
                parent_node = child_node.parent;
                if (parent_node.left.equals(child_node)){
                    prefix_code += "1";
                }
                else if (parent_node.right.equals(child_node)){
                    prefix_code += "0";
                }
                child_node = parent_node;
            }
            for (int i = prefix_code.length()-1; i>=0; i--){
                reversed += prefix_code.charAt(i);
            }
            encodeMap.put(current_node.character, reversed);
        }
        return encodeMap;
    }

    public HashMap<String, Byte> get_decodeMap(Node node){
        HashMap<String, Byte> decodeMap = new HashMap<>();
        Stack<Node> nodes = new Stack<>();
        traversal(node, nodes);
        while (!nodes.empty()){
            Node current_node = nodes.pop();
            Node child_node = current_node;
            Node parent_node;
            String prefix_code = "";
            String reversed = "";
            while (child_node.parent!=null){
                parent_node = child_node.parent;
                if (parent_node.left.equals(child_node)){
                    prefix_code += "1";
                }
                else if (parent_node.right.equals(child_node)){
                    prefix_code += "0";
                }
                child_node = parent_node;
            }
            for (int i = prefix_code.length()-1; i>=0; i--){
                reversed += prefix_code.charAt(i);
            }
            decodeMap.put(reversed, current_node.character);
        }
        return decodeMap;
    }

    public void traversal(Node node, Stack<Node> nodes){
        if (node.left==null && node.right==null){
            nodes.add(node);
            return;
        }
        traversal(node.left, nodes);
        traversal(node.right, nodes);
    }

    public void sort_nodes(Node[] nodes){
        //bubble sort
        //push node with lowest frequency towards the end of the array
        for (int i = 0; i < nodes_size(nodes)-1; i++){
            for (int j = i; j < nodes_size(nodes)-1; j++){
                if (nodes[j].frequency<nodes[j+1].frequency){
                    Node tempNode = nodes[j];
                    nodes[j] = nodes[j+1];
                    nodes[j+1] = tempNode;
                }
            }
        }
    }

    public int nodes_size(Node[] nodes){
        int size = 0;
        for (int i = 0; i < nodes.length; i++){
            if (nodes[i]!=null){
                size++;
            }
        }
        return size;
    }

    public static void main(String[] args){
        Huffman huffman = new HuffmanSubmit();
        huffman.encode("ur.jpg", "ur.enc", "ur_freq.txt");
        huffman.encode("alice30.txt", "alice30.enc", "alice30_freq.txt");
        huffman.decode("ur.enc", "ur_dec.jpg", "ur_freq.txt");
        huffman.decode("alice30.enc", "alice30_dec.txt", "alice30_freq.txt");
    }

}

class Node{
    Node parent;
    Node left;
    Node right;
    Byte character;
    int frequency;
}