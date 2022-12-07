import com.sun.tools.javac.Main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class steganography {

    public static void main(String[] args) throws IOException {
        //encode("src/1_image.bmp","src/frog.bmp");
        decode("src/encode.bmp");
    }


    public static void encode(String pathFirstBMP,String pathSecondBMP ) throws IOException {
        byte[] firstBMP = createByteColor(pathFirstBMP);
        if(firstBMP.length == 0){
            System.out.println("Файл не найдед");
            return;
        }
        byte[] head = Arrays.copyOfRange(firstBMP,0,54);
        firstBMP = Arrays.copyOfRange(firstBMP,54,firstBMP.length);
        byte[] secondBMP = createByteColor(pathSecondBMP);
        if(firstBMP.length / 4 < secondBMP.length){
            System.out.println("Размер изображения недостаточен");
            return;
        }
        for(int i = 0; i < secondBMP.length * 4;i++){
            firstBMP[i] = (byte) (firstBMP[i] | ((secondBMP[i/4] >> (i%4*2)) & 0b11));
        }
        FileOutputStream fw = new FileOutputStream("src/encode.bmp");
        fw.write(head);
        fw.write(firstBMP);
        fw.flush();
        fw.close();
    }


    private static void decode(String pathBMP){
        try {
            byte[] bmp = new FileInputStream(pathBMP).readAllBytes();
            byte[] encodeHeader = Arrays.copyOfRange(bmp, 54,270);
            byte[] decodeHeader = new byte[54];
            for(int i = 0; i < 216;i++){
                decodeHeader[i/4] = (byte) (decodeHeader[i/4] | ((encodeHeader[i] & 0b11) << (2*(i%4))));
            }
            int biHeight = ((decodeHeader[21] & 0xFF) << 24) + ((decodeHeader[20] & 0xFF) << 16) + ((decodeHeader[19] & 0xFF) << 8) + (decodeHeader[18] & 0xFF);
            int biWidth = ((decodeHeader[25] & 0xFF) << 24) + ((decodeHeader[24] & 0xFF) << 16) + ((decodeHeader[23] & 0xFF) << 8) + (decodeHeader[22] & 0xFF);
            int PixelCount = biHeight * biWidth * 3;
            byte[] encodeImg = Arrays.copyOfRange(bmp,270,bmp.length);
            byte[] decodeImg = new byte[PixelCount];
            for(int i = 0; i < PixelCount * 4;i++){
                decodeImg[i/4] = (byte) (decodeImg[i/4] | ((encodeImg[i] & 0b11) << (2*(i%4))));
            }
            FileOutputStream fw = new FileOutputStream("src/decodeBMP.bmp");
            fw.write(decodeHeader);
            fw.write(decodeImg);
            fw.close();
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private static byte[] createByteColor(String BMPFile){
        try {
            FileInputStream inputStream = new FileInputStream(BMPFile);
            return inputStream.readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new byte[0];
    }


}
