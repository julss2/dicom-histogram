import com.pixelmed.display.SingleImagePanel;
import com.pixelmed.display.SourceImage;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 *   Klasa <code>MyMainFrame</code> zajmuje sie wyświetlaniem funkcji obsługujących pliki DICOM.
 *
 *   @author Julia Walczyna
 *   @version 1.0.0   07/02/2024
 */

public class MyMainFrame extends JFrame {
    /**
     * Przycisk zapisujący liczby ustawione na spinnerach.
     */
    private JButton buttonOK;
    /**
     * Spinner przyjmujący dolny zakres modyfikacji histogramu.
     */
    private JSpinner spinner1;
    /**
     * Spinner przyjmujący górny zakres modyfikacji histogramu.
     */
    private JSpinner spinner2;
    /**
     * Przycisk resetujący zakres na spinnerach do warości domyślnej 0-256.
     */
    private JButton buttonClear;
    /**
     * Etykieta podpisująca oryginalne zdjęcie.
     */
    private JLabel labelOriginal;
    /**
     * Etykieta podpisująca zmodyfikowane zdjęcie z rozciągniętym histogramem.
     */
    private JLabel labelStretched;
    /**
     * Przycisk wyboru pliku z pamięci komputera.
     */
    private JButton buttonChoose;
    /**
     * Etykieta "od".
     */
    private JLabel label1;
    /**
     * Etykieta "do".
     */
    private JLabel label2;
    /**
     * Etykieta "Wybierz zakres do histogramu".
     */
    private JLabel label3;
    /**
     * Etykieta podpisująca zmodyfikowane zdjęcie z wyrównanym histogramem.
     */
    private JLabel labelAlignedImg;
    /**
     * Panel, na którym znajdują się wszystkie elementy ramki okna głównego.
     */
    private JPanel panelMain;
    /**
     * Panel na spinnery oraz przyciski Ok, Clear, Choose.
     */
    private JPanel panelLeft;
    /**
     * Panel, na którym wyświetlany jest histogram.
     */
    private JPanel panelHistogram;
    /**
     * Panel, na którym wyświetlane jest oryginalne zdjęcie.
     */
    private JPanel panelOriginalImage;
    /**
     * Panel, na którym wyświetlany jest rozciągniety histogram.
     */
    private JPanel panelHistogramStretched;
    /**
     * Panel, na którym wyświetlany jest znormalizowany histogram.
     */
    private JPanel panelHistogramEqualized;
    /**
     * Ścieżka do wczytywanego pliku.
     */
    private String filePath;
    /**
     * Obiekt o atrybutach wczytanego zdjęcia DICOM.
     */
    private DICOMImageDAO DICOMImg;

    /**
     * Konstruktor klasy MyMainFrame.
     * Ustawia zakres na spinnerach i obsługuje akcje wykonane przez użytkownika
     *
     *
     */
    public MyMainFrame() {
        setSpinnerModel();
        buttonChoose.addActionListener(new ActionListener() {
            /**
             * Umożliwia wybór pliku i wyświetla obraz i histogramy.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                if (chooseFile()) {
                    setDICOMImg(filePath);
                    displayImageOriginal(DICOMImg);
                    displayHistogram(DICOMImg, 1, 0, 256);
                    displayHistogram(DICOMImg, 3, 0, 256);
                }

            }
        });
        buttonOK.addActionListener(new ActionListener() {
            /**
             * Przyjmuje liczby ze spinnerów i wyświetla histogram w tym zakresie.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {

                if(filePath == null){
                    JOptionPane.showMessageDialog(null, "Brak wybranego pliku",
                            "Warning",JOptionPane.WARNING_MESSAGE);
                    return;
                }
                try {
                    spinner1.commitEdit();
                } catch ( java.text.ParseException d ) {
                    throw new RuntimeException();
                }
                int rangeFrom = (Integer) spinner1.getValue();

                try {
                    spinner2.commitEdit();
                } catch ( java.text.ParseException d ) {
                    throw new RuntimeException();
                }
                int rangeTo = (Integer) spinner2.getValue();

                if ((rangeFrom <= rangeTo) && (rangeFrom >= 0 && rangeFrom <= 256) & (rangeTo >= 0 & rangeTo <= 256)) {
                    // Przekształć histogram
                    //                   int[] transformedPixels;
//                    int[] stretchedHistogram = histogramTransformation.stretchHistogram(rangeFrom, rangeTo); // Rozciągnięcie histogramu
//                    if (rangeFrom == 0 && rangeTo == 256) {
//                        transformedPixels = DICOMImg.getIntPixelsData(); // Bez zmian
//                    } else {
//                        transformedPixels = histogramTransformation.generatePixelsValuesFromHistogram(stretchedHistogram); // Rozciągnięcie histogramu
//                    }
//
//                    // Wyświetl przekształcony obraz
//                    displayImageStretched(transformedPixels, DICOMImg.getWidth(), DICOMImg.getHeight());
                    // Wyświetl histogram
                    displayHistogram(DICOMImg, 2, rangeFrom, rangeTo);

                } else {
                    // Zły zakres, wyświetl komunikat
                    JOptionPane.showMessageDialog(null, "Nieodpowiedni zakres.",
                            "Warning",JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        buttonClear.addActionListener(new ActionListener() {
            /**
             * Ustawia wartości domyślne na spinnerach.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                spinner1.setValue(0);
                spinner2.setValue(256);
            }
        });


    }

    /**
     * Wczytuje zdjęcie ze ścieżki jako parametr klasy.
     * @param path ścieżka do pliku
     */
    private DICOMImageDAO setDICOMImg(String path) {
        DICOMImg = new DICOMImageDAO(path);
        return DICOMImg;
    }

    /**
     * Ogranicza wybór zakresu na spinnerach do 0-256.
     */
    private void setSpinnerModel() {
        SpinnerNumberModel spinnerNumberModel1 = new SpinnerNumberModel(0,0,256,1);
        SpinnerNumberModel spinnerNumberModel2 = new SpinnerNumberModel(256,0,256,1);
        spinner1.setModel(spinnerNumberModel1);
        spinner2.setModel(spinnerNumberModel2);
    }

    /**
     * Umożliwia wybór pliku przez użytkownika
     * i zapisuje jego ścieżkę do zmiennej <b>filePath</b> obiektu.
     * @return true, jeśli wczytano ścieżkę
     */
    private boolean chooseFile(){
        try {
            JFileChooser chooser = new JFileChooser();
            // filtr dla dicom
            FileNameExtensionFilter filter = new FileNameExtensionFilter("DICOM", "dcm"); //filtr pokazujacy tylko pliki dicom
            chooser.setFileFilter(filter); // ustawienie filtru
            chooser.setCurrentDirectory(new File(".")); //filechooser domyslnie pokazuje po otwarciu folder tą z apką
            int result = chooser.showOpenDialog(null);

            if (result == JFileChooser.APPROVE_OPTION) {    //jeśli wszystko ok - pobierz filepath
                filePath = chooser.getSelectedFile().getAbsolutePath();
                // jesli plik nie jest DICOM wyswietl komunikat
                if (!IsValidType(filePath, "dcm")){
                    JOptionPane.showMessageDialog(null, "Wybrano plik z nieprawidłowym rozszerzeniem.",
                            "Warning",JOptionPane.WARNING_MESSAGE);
                    return false;
                }
            }
        } catch (Exception e) {
            System.out.println("Błąd podczas wyboru pliku:");
            e.printStackTrace(); //info o błędzie
        }
        return true;
    }

    /**
     * Sprawdza czy wybrany plik ma rozszerzenie .dcm
     * @param filepath ścieżka wybranego pliku
     * @param fileType docelowe rozszerzenie pliku
     * @return true, jeśli plik ma odpowiednie rozszerzenie
     */
    public boolean IsValidType(String filepath, String fileType) {
        File fileToCheck = new File(filepath);

        if(!fileToCheck.exists()) {
            return false;
        }

        String extension = "";
        int i = filepath.lastIndexOf('.');
        if (i >= 0) {
            extension = filepath.substring(i + 1);
        }
        return extension.equals(fileType);

    }

    /**
     * Dodaje oryginalne zdjęcie <b>panelOriginalImage</b> w oknie aplikacji.
     * @param DICOMImageObject obiekt z atrybutami wczytanego zdjęcia
     */

    private void displayImageOriginal(DICOMImageDAO DICOMImageObject) {
        try {
            SourceImage img = DICOMImageObject.getSourceImage();
            panelOriginalImage.removeAll();
            panelOriginalImage.setLayout(new BorderLayout());
            panelOriginalImage.add(new SingleImagePanel(img));
            panelOriginalImage.validate();
            panelOriginalImage.setVisible(true);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Dodaje wykres do <b>panelHistogram</b> w oknie aplikacji
     * @param chart wygenerowany histogram w <code>displayHistogram</code>
     * @param panel panel JPanel, na którym wyświetli się histogram
     * @see MyMainFrame#displayHistogram(DICOMImageDAO, int, int, int)
     */
    private void addPanel(JFreeChart chart, JPanel panel) {
        ChartPanel chartPanel = new ChartPanel(chart){
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(500, 350);
            }
        };
        chartPanel.setMouseWheelEnabled(true);
        panel.removeAll();
        panel.setLayout(new BorderLayout());
        panel.add(chartPanel, BorderLayout.CENTER);
        panel.validate(); //revalidate?
        panel.setVisible(true);
    }

    /**
     * Generuje histogram na podstawie tablicy pikseli klasy <code>Histogram</code>
     * @param DICOMImageObject obraz DICOM
     * @param key przełącznik do switch'a: 1- oryginalny, 2- rozciągnięty, 3- wyrównany histogram
     * @param range1 zakres modyfikacji histogramu (dolna granica) od 0 do 256
     * @param range2 zakres modyfikacji histogramu (górna granica) od 0 do 256
     * @see Histogram
     */
    private void displayHistogram(DICOMImageDAO DICOMImageObject, int key, int range1, int range2) {
        // deklaracja zmiennych
        String title = "";
        XYSeries series = new XYSeries("Histogram");

        // utworzenie tablicy pikseli
//        zmienione po zmianie dicomimagedao.java
        int[] grayscalePixelValues = DICOMImageObject.getIntPixelsData();
        JPanel panel = new JPanel();
        switch(key) {
            case 1:
                title = "Original histogram";
                Histogram histogram1 = new Histogram(DICOMImageObject, grayscalePixelValues);
                histogram1.generateHistogramData();
                int[] histogramData = histogram1.getHistogram();
                for (int i = 1; i < 256; i++) {
                    series.add(i, histogramData[i]);
                }
                panel = panelHistogram;
                break;

            case 2:
                title = "Stretched histogram";
                HistogramTransformation histogram2 = new HistogramTransformation(DICOMImageObject, grayscalePixelValues);
                int[] histogramData2 = histogram2.stretchHistogram(range1, range2);
                // histogram2.generatePixelsValuesFromHistogram(histogramData2);
                for (int i = 1; i < 256; i++) {
                    series.add(i, histogramData2[i]);
                }
                panel = panelHistogramStretched;
                break;

            case 3:
                title = "Equalized Histogram";
                HistogramTransformation histogram3 = new HistogramTransformation(DICOMImageObject, grayscalePixelValues);
                int[] histogramData3 = histogram3.equalizeHistogram(); //funkcja wyrownujaca
                for (int i = 1; i < 256; i++) {
                    series.add(i, histogramData3[i]);
                }
                panel = panelHistogramEqualized;
                break;
        }
        // stwórz wykres
        XYSeriesCollection dataset = new XYSeriesCollection(series);
        JFreeChart barChart = ChartFactory.createXYBarChart(
                title,
                "Pixel value",
                false, // Pominiecie legendy
                "Number of occurrences",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );
        //dodaj i wyswietl wykres
        addPanel(barChart, panel);
    }

    /**
     * Uruchamia program
     * @param args
     */
    public static void main(String[] args) {
        //Tworzenie i pokazywanie głównego okna
        MyMainFrame frame = new MyMainFrame();
        frame.setContentPane(frame.panelMain);
        frame.pack();
        frame.setTitle("DICOM editor");
        frame.setSize(1500, 800);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}