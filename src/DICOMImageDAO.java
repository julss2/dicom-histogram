import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.OtherWordAttribute;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.display.SourceImage;

import java.util.Arrays;

/**
 * Klasa DICOMImageDAO służy do obsługi obrazów w formacie DICOM.
 *
 * @author Julia Rozmarynowska
 * @version 1.0.0 07/02/2024
 */

public class DICOMImageDAO {
    /**
     * Struktura danych do przechowywania informacji o obrazie
     */
    private AttributeList dcmInfo;
    /**
     * Obiekt służący do obsługi obrazu
     */
    private SourceImage dcmImg;
    /**
     * Szerokość obrazu
     */
    private int width;
    /**
     * Wysokość obrazu
     */
    private int height;
    /**
     * Minimalna wartość piksela
     */
    private int minPixel;
    /**
     * Maksymalna wartość piksela
     */
    private int maxPixel;
    /**
     * Tablica przechowująca wartości pikseli obrazu jako liczby całkowite
     */
    private int[] intPixelsData;
    /**
     * Ścieżka do pliku
     */
    private String filePath;
    /**
     * Konstruktor klasy DICOMImageDAO.
     *
     * @param filepath Ścieżka do pliku DICOM.
     */
    public DICOMImageDAO(String filepath) {
        filePath = filepath; //przypisuje sciezke pliku do atrybutu klasy
        readDICOMImage(filePath);
    }
    /**
     * Metoda zwracająca szerokość obrazu.
     *
     * @return Szerokość obrazu.
     */
    public int getWidth(){
        return width;
    }
    /**
     * Metoda zwracająca wysokość obrazu.
     *
     * @return Wysokość obrazu.
     */
    public int getHeight(){
        return height;
    }
    /**
     * Metoda zwracająca minimalną wartość piksela.
     *
     * @return Minimalna wartość piksela.
     */
    public int getMinPixel(){
        return minPixel;
    }
    /**
     * Metoda zwracająca maksymalną wartość piksela.
     *
     * @return Maksymalna wartość piksela.
     */
    public int getMaxPixel(){
        return maxPixel;
    }
    /**
     * Metoda zwracająca dane pikseli jako tablicę.
     *
     * @return Tablica danych pikseli.
     */
    public int[] getIntPixelsData() {
        return intPixelsData;
    }
    /**
     * Metoda zwracająca obiekt SourceImage.
     *
     * @return Obiekt SourceImage.
     */
    public SourceImage getSourceImage() {
        return dcmImg;
    }
    /**
     * Prywatna metoda do odczytu obrazu DICOM.
     *
     * @param filePath Ścieżka do pliku DICOM.
     */
    private void readDICOMImage(String filePath) {
        try {
            // AttributeList jest strukturą danych używaną w obszarze DICOM do przechowywania i zarządzania danymi o atrybutach obrazów medycznych w formacie DICOM
            // WCZYTAJ OBRAZ DICOM
            dcmInfo = new AttributeList();
            dcmInfo.read(filePath);

            // SourceImage jest klasą służącą do obsługi obrazów medycznych w formacie DICOM
            dcmImg = new SourceImage(dcmInfo);

            // SZEROKOŚĆ I WYSOKOŚĆ OBRAZU
            width = dcmImg.getWidth();
            height = dcmImg.getHeight();

            // DANE PIKSELI OBRAZU 16-BITOWEGO
            OtherWordAttribute pixelsAttribute = (OtherWordAttribute) (dcmInfo.get(TagFromName.PixelData)); // OtherWordAttribute służy do odczytywania danych o pikelach, które są przechowywane jako 16-bitowe(short) wartości (tak zakładamy) w atrybucie Pixel Data
            short[] shortPixelsData = pixelsAttribute.getShortValues();

            // KONWERSJA NA WARTOŚCI INT
            intPixelsData = new int[shortPixelsData.length];
            for (int i = 0; i < shortPixelsData.length; i++) {
                intPixelsData[i] = shortPixelsData[i] & 0xFFFF; //KONWERSJA SHORT NA INT
            }

            // MINIMALNA I MAKSYMALNA WARTOŚĆ PIKSELA
            minPixel = Arrays.stream(intPixelsData).min().orElse(0); // Arrays.stream(intPixelsData) tworzy strumień z tablicy intPixelsData, a następnie min() szuka minimalnej wartości w tym strumieniu
            maxPixel = Arrays.stream(intPixelsData).max().orElse(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
