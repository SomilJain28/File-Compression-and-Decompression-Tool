import java.io.*;
import java.util.*;

public class HuffmanCoding {
    private Map<Character, String> charPrefixMap = new HashMap<>();
    private PriorityQueue<HuffmanNode> priorityQueue;
    
    public void compress(String inputFile, String outputFile) throws IOException {
        String text = readFile(inputFile);
        Map<Character, Integer> frequencyMap = buildFrequencyMap(text);

        priorityQueue = new PriorityQueue<>(Comparator.comparingInt(node -> node.frequency));
        frequencyMap.forEach((k, v) -> {
            HuffmanNode node = new HuffmanNode();
            node.character = k;
            node.frequency = v;
            priorityQueue.add(node);
        });

        HuffmanNode root = buildTree();

        buildPrefixMap(root, "");

        StringBuilder encodedString = new StringBuilder();
        for (char character : text.toCharArray()) {
            encodedString.append(charPrefixMap.get(character));
        }

        writeCompressedFile(outputFile, encodedString.toString(), charPrefixMap);
    }

    public void decompress(String inputFile, String outputFile) throws IOException {
        DataInputStream inputStream = new DataInputStream(new FileInputStream(inputFile));
        int mapSize = inputStream.readInt();
        charPrefixMap = new HashMap<>();

        for (int i = 0; i < mapSize; i++) {
            char character = inputStream.readChar();
            String prefix = inputStream.readUTF();
            charPrefixMap.put(character, prefix);
        }

        StringBuilder encodedString = new StringBuilder();
        while (inputStream.available() > 0) {
            encodedString.append(inputStream.readByte() == 1 ? '1' : '0');
        }
        inputStream.close();

        Map<String, Character> prefixCharMap = new HashMap<>();
        charPrefixMap.forEach((k, v) -> prefixCharMap.put(v, k));

        HuffmanNode root = buildTreeFromPrefixMap();
        HuffmanNode temp = root;
        StringBuilder decodedString = new StringBuilder();

        for (char bit : encodedString.toString().toCharArray()) {
            temp = bit == '1' ? temp.right : temp.left;
            if (temp.left == null && temp.right == null) {
                decodedString.append(temp.character);
                temp = root;
            }
        }

        writeFile(outputFile, decodedString.toString());
    }

    private String readFile(String inputFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        return sb.toString();
    }

    private void writeFile(String outputFile, String data) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
        writer.write(data);
        writer.close();
    }

    private Map<Character, Integer> buildFrequencyMap(String text) {
        Map<Character, Integer> frequencyMap = new HashMap<>();
        for (char c : text.toCharArray()) {
            frequencyMap.put(c, frequencyMap.getOrDefault(c, 0) + 1);
        }
        return frequencyMap;
    }

    private HuffmanNode buildTree() {
        while (priorityQueue.size() > 1) {
            HuffmanNode x = priorityQueue.poll();
            HuffmanNode y = priorityQueue.poll();
            HuffmanNode sum = new HuffmanNode();
            sum.frequency = x.frequency + y.frequency;
            sum.left = x;
            sum.right = y;
            priorityQueue.add(sum);
        }
        return priorityQueue.poll();
    }

    private void buildPrefixMap(HuffmanNode node, String prefix) {
        if (node.left == null && node.right == null) {
            charPrefixMap.put(node.character, prefix);
            return;
        }
        buildPrefixMap(node.left, prefix + '0');
        buildPrefixMap(node.right, prefix + '1');
    }

    private void writeCompressedFile(String outputFile, String encodedString, Map<Character, String> charPrefixMap) throws IOException {
        DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(outputFile));
        outputStream.writeInt(charPrefixMap.size());
        for (Map.Entry<Character, String> entry : charPrefixMap.entrySet()) {
            outputStream.writeChar(entry.getKey());
            outputStream.writeUTF(entry.getValue());
        }

        for (char bit : encodedString.toCharArray()) {
            outputStream.writeByte(bit == '1' ? 1 : 0);
        }
        outputStream.close();
    }

    private HuffmanNode buildTreeFromPrefixMap() {
        HuffmanNode root = new HuffmanNode();
        for (Map.Entry<Character, String> entry : charPrefixMap.entrySet()) {
            HuffmanNode temp = root;
            String prefix = entry.getValue();
            for (char c : prefix.toCharArray()) {
                if (c == '0') {
                    if (temp.left == null) {
                        temp.left = new HuffmanNode();
                    }
                    temp = temp.left;
                } else {
                    if (temp.right == null) {
                        temp.right = new HuffmanNode();
                    }
                    temp = temp.right;
                }
            }
            temp.character = entry.getKey();
        }
        return root;
    }
}
