/**
 * Klasa Histogram służy do generowania i analizowania histogramów obrazów DICOM.
 * @author Julia Rozmarynowska
 * @version 1.0.0 07/02/2024
 */
public class Histogram {
    /**
     * Obiekt DICOMImageDAO reprezentujący obraz DICOM.
     */
    protected DICOMImageDAO dicomImageDAO;
    /**
     * Tablica wartości pikseli, dla których zostanie wygenerowany histogram.
     */
    protected int[] pixelsValues;
    /**
     * Statyczna tablica przechowująca dane histogramu.
     */
    private static int[] histogram;
    /**
     * Konstruktor klasy Histogram.
     *
     * @param dicomImageDAO Obiekt DICOMImageDAO reprezentujący obraz DICOM.
     * @param pixelsMap Tablica wartości pikseli, dla których zostanie wygenerowany histogram.
     */
    public Histogram(DICOMImageDAO dicomImageDAO, int[] pixelsMap) {
        this.dicomImageDAO = dicomImageDAO;
        this.pixelsValues = pixelsMap;
        this.histogram = generateHistogramData();
    }
    /**
     * Metoda statyczna zwracająca histogram.
     *
     * @return Tablica danych histogramu.
     */
    public static int[] getHistogram() {

        return histogram;
    }
    /**
     * Metoda generująca dane histogramu na podstawie wartości pikseli.
     *
     * @return Tablica danych histogramu.
     */
    public int[] generateHistogramData() {
        int[] histogram = new int[256];

        // OBLICZAM PROPORCJĘ, JAKĄ TRZEBA PRZESKALOWAĆ PIKSELE, ABY MIEŚCIŁY SIĘ W ZAKRESIE OD 0 DO 255
        int maxPixelValue = dicomImageDAO.getMaxPixel();
        int minPixelValue = dicomImageDAO.getMinPixel();
        double scale = (double) (maxPixelValue - minPixelValue + 1) / 256.0;

        // OBLICZAM INDEKS, KTÓRY JEST UŻYWANY DO ZAKTUALIZOWANIA ODPOWIEDNIEGO PRZEDZIAŁU W TABLICY HISTOGRAM
        for (int pixelValue : pixelsValues) {
            int index = (int) ((pixelValue - minPixelValue) / scale);
            histogram[index]++;
        }

        return histogram;
    }
}

