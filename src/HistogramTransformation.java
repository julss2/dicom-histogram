/**
 * Klasa HistogramTransformation rozszerza klasę Histogram i zawiera metody do transformacji histogramu obrazu.
 * @author Julia Rozmarynowska
 * @version 1.0.0 07/02/2024
 */
public class HistogramTransformation extends Histogram {
    /**
     * Konstruktor klasy HistogramTransformation.
     *
     * @param dicomImageDAO Obiekt DICOMImageDAO reprezentujący obraz DICOM.
     * @param pixelsValues Tablica wartości pikseli, dla których zostanie wygenerowany histogram.
     */
    public HistogramTransformation(DICOMImageDAO dicomImageDAO, int[] pixelsValues) {
        super(dicomImageDAO, pixelsValues);
    }
    /**
     * Metoda rozciągająca histogram w zadanym zakresie.
     *
     * @param start Wartość początkowa zakresu.
     * @param stop  Wartość końcowa zakresu.
     * @return Tablica danych histogramu po rozciągnięciu.
     */
    public int[] stretchHistogram(int start, int stop) {
        int[] originalHistogram = getHistogram();
        int[] stretchedHistogram = new int[256];

        // OBLICZAM ILE WARTOŚCI PIKSELI BĘDZIE MIAŁO TAKIE SAME WARTOŚCI PO ROZCIĄFGNIĘCIU
        double scale = 255.0 / (stop - start);

        // ITERTUJĘ PO WYBRANYM ZAKRESIE ORYGINALNEGO HISTOGRAMU
        for (int index = start; index < stop; index++) {
            int pixelsAmount = originalHistogram[index];

            // OBLICZAM ZAKRES INDEKSÓW KTÓRYM W DANEJ PĘTLI BĘDĄ PRZYPISYWANE ILOŚCI PIKSELI
            int scaledIndexStart = (int) ((index - start) * scale);
            int scaledIndexStop = (int) ((index - start + 1) * scale);

            // PRZYPISUJĘ PIXELSAMOUNT DO ODPOWIEDNIEGO ZAKRESU INEKSÓW
            for (int scaledIndex = scaledIndexStart; scaledIndex < scaledIndexStop; scaledIndex++) {
                if (scaledIndex >= 0 && scaledIndex < 256) {
                    stretchedHistogram[scaledIndex] = pixelsAmount;
                }
            }
        }

        return stretchedHistogram;
    }
    /**
     * Metoda wyrównująca histogram.
     *
     * @return Tablica danych histogramu po wyrównaniu.
     */
    public int[] equalizeHistogram() {
        int[] originalHistogram = getHistogram();
        int totalPixels = pixelsValues.length;
        double[] normalizedHistogram = new double[256];

        // OBLICZAM ZNORMALIZOWANY HISTOGRAM
        for (int i = 0; i < 256; i++) {
            normalizedHistogram[i] = (double) originalHistogram[i] / totalPixels;
        }

        // OBLICZAM DYSTRYBUANTĘ SKUMULOWANĄ
        double[] cdf = new double[256];
        cdf[0] = normalizedHistogram[0];
        for (int i = 1; i < 256; i++) {
            cdf[i] = cdf[i - 1] + normalizedHistogram[i];
        }

        // WYRÓWNUJĘ HISTOGRAM
        int[] equalizedHistogram = new int[256];
        for (int i = 0; i < 256; i++) {
            equalizedHistogram[i] = (int) (cdf[i] * 255);
        }

        // STOSUJĘ FUNKCJE PRZEJŚCIOWĄ DO ORYGINALNEGO HISTOGRAMU
        int[] resultHistogram = new int[256];
        for (int i = 0; i < 256; i++) {
            resultHistogram[equalizedHistogram[i]] = originalHistogram[i];
        }

        return resultHistogram;
    }
}

