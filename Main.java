import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        String inputFile = "input.txt";
        String compressedFile = "compressed.huff";
        String decompressedFile = "decompressed.txt";

        HuffmanCoding huffmanCoding = new HuffmanCoding();

        huffmanCoding.compress(inputFile, compressedFile);
        System.out.println("File compressed successfully!");

        huffmanCoding.decompress(compressedFile, decompressedFile);
        System.out.println("File decompressed successfully!");
    }
}
