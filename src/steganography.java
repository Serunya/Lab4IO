import com.sun.tools.javac.Main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class steganography {

    public static void main(String[] args) throws IOException {
        encode("src/1_image.bmp","src/frog.bmp",4);
        System.out.println("decode");
        decode("src/encode.bmp",4);
    }

    public static void encode(String pathFirstBMP,String pathSecondBMP,int countByte) throws IOException {
        byte[] firstBMP = createByteColor(pathFirstBMP);
        if(firstBMP.length == 0){
            System.out.println("Файл не найдед");
            return;
        }

        int countIMG = 8 / countByte;
        byte mb = (byte) 0b1;
        for(int j = 1; j < countByte;j++){
            mb = (byte) (mb << 1 | 0b1);
        }
        byte mC = (byte) (0b11111111 ^ mb);
        mb = (byte) (mb & 0xFF);
        mC = (byte) (mC & 0xFF);
        byte[] head = Arrays.copyOfRange(firstBMP,0,54);
        firstBMP = Arrays.copyOfRange(firstBMP,54,firstBMP.length);
        byte[] secondBMP = createByteColor(pathSecondBMP);
        if(firstBMP.length / countIMG < secondBMP.length){
            System.out.println("Размер изображения недостаточен");
            return;
        }
        for(int i = 0; i < secondBMP.length * countIMG;i++){
            firstBMP[i] = (byte) ((firstBMP[i] & mC ) | ((secondBMP[i/countIMG] >> (i%countIMG*countByte)) & mb));
        }
        FileOutputStream fw = new FileOutputStream("src/encode.bmp");
        fw.write(head);
        fw.write(firstBMP);
        fw.flush();
        fw.close();
    }


    private static void decode(String pathBMP,int countByte){
        try {
            int countIMG = 8 / countByte;
            byte mb = (byte) 0b1;
            for(int j = 1; j < countByte;j++){
                mb = (byte) (mb << 1 | 0b1);
            }
            byte[] bmp = new FileInputStream(pathBMP).readAllBytes();
            byte[] encodeHeader = Arrays.copyOfRange(bmp, 54,54*countIMG+54);
            byte[] decodeHeader = new byte[54];
            for(int i = 0; i < 54*countIMG;i++){
                decodeHeader[i/countIMG] = (byte) (decodeHeader[i/countIMG] | ((encodeHeader[i] & mb) << (countByte*(i%countIMG))));
            }
            int biHeight = ((decodeHeader[21] & 0xFF) << 24) + ((decodeHeader[20] & 0xFF) << 16) + ((decodeHeader[19] & 0xFF) << 8) + (decodeHeader[18] & 0xFF);
            int biWidth = ((decodeHeader[25] & 0xFF) << 24) + ((decodeHeader[24] & 0xFF) << 16) + ((decodeHeader[23] & 0xFF) << 8) + (decodeHeader[22] & 0xFF);
            int PixelCount = biHeight * biWidth * 3;
            byte[] encodeImg = Arrays.copyOfRange(bmp,54*countIMG+54,bmp.length);
            byte[] decodeImg = new byte[PixelCount];
            for(int i = 0; i < PixelCount * countIMG;i++){
                decodeImg[i/countIMG] = (byte) (decodeImg[i/countIMG] | ((encodeImg[i] & mb) << (countByte*(i%countIMG))));
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
