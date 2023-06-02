import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

class DosyaTipiBelirleme {
    //TÜM DOSYALARI SEÇEN FONKSİYON
    public List<String> tumDosyalar(String kaynakKlasorYolu) {
        List<String> tumDosyaYollari = new ArrayList<>(); //İstenen uzantılı tüm dosyaların toplanacağı bir liste oluşturuldu.
        File kaynakKlasor = new File(kaynakKlasorYolu);

        if (kaynakKlasor.exists() && kaynakKlasor.isDirectory()) {
            File[] tumDosyalar = kaynakKlasor.listFiles();
            if(tumDosyalar!=null) {
                for (File tumDosyaDizisi : tumDosyalar) {
                    if (tumDosyaDizisi.isFile()) {
                        tumDosyaYollari.add(tumDosyaDizisi.getAbsolutePath());
                    }
                }
            }
            else {
                System.out.println("Klasör bulunamadı.");
            }
        }
        return tumDosyaYollari; //En başta oluşturulmuş olan istenen uzantılı dosyalar listesini List<String> tipinde return eder.
    }
    //İSTENEN UZANTILI DOSYALARI SEÇEN FONKSİYON
    public List<String> dosyaTipiSecme(String kaynakKlasorYolu,String tip) {
        List<String> istenenDosyaYollari = new ArrayList<>();
        File kaynakKlasor = new File(kaynakKlasorYolu);

        if (kaynakKlasor.exists() && kaynakKlasor.isDirectory()) {
            File[] pdfDosyalar = kaynakKlasor.listFiles();
            if(pdfDosyalar!=null) {
                for (File pdfDosyaDizisi : pdfDosyalar) {
                    if (pdfDosyaDizisi.isFile() && pdfDosyaDizisi.getName().endsWith(tip)) {
                        istenenDosyaYollari.add(pdfDosyaDizisi.getAbsolutePath());
                    }
                }
            }
            else {
                System.out.println("Klasör bulunamadı.");
            }
        }
        return istenenDosyaYollari;
    }
}
//----------------------------------------------------------------------------------------------------------------------
class DosyaIslemleri {

    //DOSYA TAŞIMA İŞLEMİNİ GERÇEKLEŞTİREN FONKSİYON.
    public void dosyaTasima(List<String> tasinacakDosyalar, String hedefKlasorYolu) {
        String[] tasinacakDosyalarDizisi = tasinacakDosyalar.toArray(new String[0]);

        for(String dosyaYolu : tasinacakDosyalarDizisi) {
            try {
                File hedefKlasor = new File(hedefKlasorYolu);
                File tasinacakDosya = new File(dosyaYolu);
                Path hedefKlasorPath = hedefKlasor.toPath();
                Path tasinacakDosyaPath = tasinacakDosya.toPath();
                Files.move(tasinacakDosyaPath, hedefKlasorPath.resolve(tasinacakDosyaPath.getFileName()), StandardCopyOption.REPLACE_EXISTING);
                //resolve() methodu ile 'hedefKlasorPath' yolunun üzerine 'tasinacakDosyaPath' yolunun dosya adı eklenir.
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Dosya taşıma işlemi başarıyla gerçekleştirildi.");
    }

    //DOSYA ŞİFRELEME İŞLEMİNİ GERÇEKLEŞTİREN FONKSİYON.
    private static final String Algoritma = "AES";
    private static final String Sifre = "sudeezeyneppodev";
    public void dosyaSifreleme(List<String> orijinalDosyalar) throws IOException, IllegalBlockSizeException,
            BadPaddingException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {

        try {
            for (String dosyaYolu : orijinalDosyalar) {
                byte[] sifreBytes = Sifre.getBytes();
                //getBytes() metodu ile String tipindeki şifremizin byte tipinde bir diziye dönüşmesi sağlandı.
                SecretKeySpec sifreFormati = new SecretKeySpec(sifreBytes, Algoritma);
                //SecretKeySpec sınıfı, şifreleme algoritmalarında kullanılan gizli anahtarları temsil etmek için kullanıldı.
                Cipher sifreleme = Cipher.getInstance(Algoritma);
                //Cipher sınıfını şifreleme ve deşifreleme işlemlerini gerçekleştirmek için kullanıldı.
                sifreleme.init(Cipher.ENCRYPT_MODE, sifreFormati);
                //init() methodu ile şifreleme işlemi başlatıldı.

                Path dosyaPath = Paths.get(dosyaYolu);
                byte[] sifrelenecekIcerik = Files.readAllBytes(dosyaPath);
                //readAllBytes() methodu ile belirtilen dosya yolunun içeriği byte dizisi olarak okunur.
                byte[] sifrelenenIcerik = sifreleme.doFinal(sifrelenecekIcerik);
                //doFinal() methodu ile parametre olarak aldığı byte dizisi şifrelenir.
                Files.write(dosyaPath, sifrelenenIcerik);
                //wrtie() methodu ile şifrelenmiş içerik yolu belirtilen dosyaya yazılır.
            }
            System.out.println("Dosya şifreleme işlemi başarıyla gerçekleştirildi.");
        } catch (Exception e) {
            System.out.println("Bir hata oluştu: " + e.getMessage());
        }
    }

    //ŞİFRE ÇÖZÜMLEME İŞLEMİNİ GERÇEKLEŞTİREN FONKSİYON.
    public void sifreCoz(List<String> sifreliDosyalar) throws IOException, NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        try {
            for (String dosyaYolu : sifreliDosyalar) {
                byte[] sifreBytes = Sifre.getBytes();
                SecretKeySpec sifreFormati = new SecretKeySpec(sifreBytes, Algoritma);
                Cipher sifreCozme = Cipher.getInstance(Algoritma);
                sifreCozme.init(Cipher.DECRYPT_MODE, sifreFormati);

                Path dosyaPath = Paths.get(dosyaYolu);
                byte[] sifreliIcerik = Files.readAllBytes(dosyaPath);
                byte[] cozulmusIcerik = sifreCozme.doFinal(sifreliIcerik);
                Files.write(dosyaPath, cozulmusIcerik);
            }
            System.out.println("Dosya şifresi başarıyla çözüldü.");
        } catch (Exception e) {
            System.out.println("Bir hata oluştu: " + e.getMessage());
        }
    }

    //DOSYA GİZLEME İŞLEMİNİ GERÇEKLEŞTİREN FONKSİYON.
    public void dosyaGizleme(List<String> gizlenecekDosyalar) throws IOException {
        String[] gizlenecekDosyaYollari = gizlenecekDosyalar.toArray(new String[0]);
        for(String dosyaYolu : gizlenecekDosyaYollari) {
            Path dosyaPath = Paths.get(dosyaYolu);
            Files.setAttribute(dosyaPath, "dos:hidden", true);
            /*setAttribute() methodu ile belirtilen dosya yolunun gizli(hidden) özelliği true yapılarak dosya gizlenir.
            Not: Windows'a göre ayarlandı!*/
        }
        System.out.println("Dosya gizleme işlemi başarıyla gerçekleştirildi.");
    }
}
//----------------------------------------------------------------------------------------------------------------------
class ZipIslemleri {

    //DOSYALARI ZİPLEME İŞLEMİNİ GERÇEKLEŞTİREN FONKSİYON.
    public void dosyaZipleme(String zipDosyaAdi, List<String> ziplenecekDosyalar, String kaynakKlasorYolu) {
        String ziplenmisKlasorYolu = kaynakKlasorYolu + "\\" + zipDosyaAdi;
        //Kullanıcı tarafından belirlenen zip ismi ile bulunduğu klasör yolu birleştirilerek zip yolu oluşturuldu.
        try {
            FileOutputStream fos = new FileOutputStream(ziplenmisKlasorYolu);
            //Dosya açar ve dosyaya byte bazında veri yazma işlemini gerçekleştirir.
            ZipOutputStream zos = new ZipOutputStream(fos);
            //Zip dosyasının içine veri eklemek için kullanır.
            String[] ziplenecekDosyaYollari = ziplenecekDosyalar.toArray(new String[0]);

            for (String dosyaYolu : ziplenecekDosyaYollari) {
                File ziplenecekDosya = new File(dosyaYolu);
                FileInputStream fis = new FileInputStream(ziplenecekDosya);
                //FileInputStream sınıfı ile dosyadan veri okumak için giriş akışı kullanılır.
                ZipEntry zipE = new ZipEntry(ziplenecekDosya.getName());
                //ZipEntry nesnesi, bir sonraki dosyayı temsil eder ve içerisindeki veriye erişmek için kullanılır.
                zos.putNextEntry(zipE);
                //putNextEntry() methodu ile zip dosyasına sırasıyla birden fazla dosya eklenir.
                byte[] geciciVeriDeposu = new byte[1024];
                //Ziplenecek dosyalardaki verilerin geçici olarak depolanması için bir byte dizisi oluşturuldu.
                int veriUzunlugu;
                while ((veriUzunlugu = fis.read(geciciVeriDeposu)) > 0) {
                    zos.write(geciciVeriDeposu, 0, veriUzunlugu);
                    //write() ile ziplenecek dosya içerisine istenen veriler yazılır.
                }
                fis.close(); //İçeriği okunan dosya kapatılır.
            }
            //Tüm okuma ve yazma işlemleri bittikten sonra...
            zos.closeEntry(); //Zip içerisine eklenen dosyaya veri girişini kapatır ve yeni bir dosyanın girişini belirtir.
            zos.close(); //Zip içerisine dosya girişini kapatarak zip'e daha fazla dosya eklenmesini engeller.

            System.out.println("Dosya zipleme işlemi başarıyla gerçekleştirildi.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //ZİP DOSYASINI TAŞIMA İŞLEMİNİ GERÇEKLEŞTİREN FONKSİYON.
    public void zipTasima(String zipDosyaAdi, String kaynakKlasorYolu, String hedefKlasorYolu) {
        String zipYol = kaynakKlasorYolu+"\\"+zipDosyaAdi;

        try {
            Path zipPath = Path.of(zipYol);
            Path hedefPath = Path.of(hedefKlasorYolu, zipPath.getFileName().toString());
            Files.move(zipPath, hedefPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Zip dosyası taşıma işlemi başarıyla gerçekleştirildi.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
//----------------------------------------------------------------------------------------------------------------------
public class Main2 extends JFrame implements ActionListener {
    private JTextField kaynakDizinField, hedefDizinField;
    private JButton sifrele_button = new JButton("Şifrele");
    private JButton sifrecoz_button = new JButton("Şifreyi Çöz");
    private JButton zip_button = new JButton("Sıkıştır");
    private JButton gizle_button = new JButton("Gizle");
    private JButton tasi_button = new JButton("Taşı");
    private ButtonGroup dosya_turu = new ButtonGroup();
    private JRadioButton tum, doc, png, pdf, txt;

    public Main2() {
        // Frame ayarları
        setTitle("Dosya Taşıma");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        // Etiketler
        JLabel kaynakDizinLabel = new JLabel("Taşınacak Dizin: ");
        JLabel hedefDizinLabel = new JLabel("Yeni Konum: ");

        // Text alanları
        kaynakDizinField = new JTextField(20);
        hedefDizinField = new JTextField(20);

        // JRadioButton'lar
        tum = new JRadioButton("Tüm Dosyalar");
        pdf = new JRadioButton("PDF");
        doc = new JRadioButton("DOC");
        txt = new JRadioButton("TXT");
        png = new JRadioButton("PNG");

        dosya_turu.add(tum);
        dosya_turu.add(pdf);
        dosya_turu.add(doc);
        dosya_turu.add(txt);
        dosya_turu.add(png);

        // Bileşenlerin konumlarını belirleme
        kaynakDizinLabel.setBounds(20, 20, 120, 25);
        kaynakDizinField.setBounds(150, 20, 300, 25);
        hedefDizinLabel.setBounds(20, 60, 120, 25);
        hedefDizinField.setBounds(150, 60, 300, 25);
        gizle_button.setBounds(20, 160, 120, 25);
        zip_button.setBounds(180, 160, 120, 25);
        sifrele_button.setBounds(340, 160, 120, 25);
        sifrecoz_button.setBounds(500, 160, 120, 25);
        tasi_button.setBounds(290, 220, 80, 25);
        tum.setBounds(20, 100, 150, 25);
        pdf.setBounds(180, 100, 80, 25);
        doc.setBounds(270, 100, 80, 25);
        txt.setBounds(360, 100, 80, 25);
        png.setBounds(450, 100, 80, 25);

        // Arayüz bileşenlerini ekleme
        add(kaynakDizinLabel);
        add(kaynakDizinField);
        add(hedefDizinLabel);
        add(hedefDizinField);
        add(tum);
        add(pdf);
        add(doc);
        add(txt);
        add(png);
        add(sifrele_button);
        add(zip_button);
        add(gizle_button);
        add(sifrecoz_button);
        add(tasi_button);

        // ActionListener'ları ekleme
        sifrele_button.addActionListener(this);
        sifrecoz_button.addActionListener(this);
        zip_button.addActionListener(this);
        gizle_button.addActionListener(this);
        tasi_button.addActionListener(this);

        // RadioButton ActionListener'ları ekleme
        tum.addActionListener(this);
        pdf.addActionListener(this);
        doc.addActionListener(this);
        txt.addActionListener(this);
        png.addActionListener(this);

        // Arayüzü gösterme
        setSize(650, 300);
        setVisible(true);
    }

    String zipDosyaAdi;
    boolean zipButtonKontrolTum, zipButtonKontrolPdf, zipButtonKontrolDoc,
            zipButtonKontrolTxt, zipButtonKontrolPng = false;
    @Override
    public void actionPerformed(ActionEvent e) {
        String kaynakDizin = kaynakDizinField.getText();
        String hedefDizin = hedefDizinField.getText();
        File kaynak = new File(kaynakDizin);
        File hedef = new File(hedefDizin);
        List<String> secilenDosyalar;
        DosyaTipiBelirleme tip = new DosyaTipiBelirleme();
        DosyaIslemleri islem = new DosyaIslemleri();
        ZipIslemleri zipIslm = new ZipIslemleri();

        if (tum.isSelected()) {
            //isSelected() methodu ile istenen dosya tipinin arayüzde seçilip seçilmediği kontrol edilir.
            secilenDosyalar = tip.tumDosyalar(kaynakDizin);

            if (e.getActionCommand().equals("Şifrele")) {
                //getActionCommand() ve equals() methodları ile istenen işlem butonuna arayüzde tıklanıp tıklanmadığı kontrol edilir.
                try {
                    islem.dosyaSifreleme(secilenDosyalar);
                    JOptionPane.showMessageDialog(this, "Tüm Dosyalar Şifrelendi.");
                    /*JOptionPane sınıfından çağırılan showMessageDialog() methodu ile kullanıcıya işlemin sonuçlandığı
                    açılan bir mesaj ekranı ile gösterilir.*/
                } catch (IOException | IllegalBlockSizeException | BadPaddingException |
                         NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException ex) {
                    throw new RuntimeException(ex);
                }
            }

            else if (e.getActionCommand().equals("Şifreyi Çöz")) {
                try {
                    islem.sifreCoz(secilenDosyalar);
                    JOptionPane.showMessageDialog(this, "Şifre çözümlendi.");
                } catch (IOException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException |
                         BadPaddingException | IllegalBlockSizeException ex) {
                    throw new RuntimeException(ex);
                }
            }

            else if (e.getActionCommand().equals("Sıkıştır")) {
                zipDosyaAdi = JOptionPane.showInputDialog(this,
                        "Oluşturmak istediğiniz zip adını sonuna '.zip' ifadesini ekleyerek giriniz:");
                /*jOptionPane sınıfından çağırılan showInputDialog() methodu ile arayüzde kullanıcının oluşturmak istediği
                zipi isimlendirmesi için karşısına bir ekran açar ve kullanıcının girmiş olduğu zip ismini kullanarak zip
                işlemlerini gerçekleştirir.*/

                if (zipDosyaAdi != null && !zipDosyaAdi.isEmpty()) {
                    zipButtonKontrolTum = true;
                    /*Zip ismi oluşturulduğunu (yani dosyaların ziplenmek istendiğini) kontrol eder ve eğer zip adı
                    oluşturulduysa zipButtonKontrol değişkenine true ataması yapar. (Daha sonra dosya taşımada kullanılacaktır.)*/
                    zipIslm.dosyaZipleme(zipDosyaAdi, secilenDosyalar, kaynakDizin);
                    JOptionPane.showMessageDialog(this, "Tüm Dosyalar Sıkıştırıldı");
                }
            }

            else if (e.getActionCommand().equals("Gizle")) {
                try {
                    islem.dosyaGizleme(secilenDosyalar);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                JOptionPane.showMessageDialog(this, "Tüm Dosyalar Gizlendi");
            }

            else if (e.getActionCommand().equals("Taşı")) {
                if (kaynak.exists() && kaynak.isDirectory()) {
                    if (hedef.exists() && hedef.isDirectory()) {

                        if(zipButtonKontrolTum) {
                        /*'Taşı' butonuna basıldıktan sonra zipKontrolButton değişkeninin true olup olmadığını kontrol eder.
                        Eğer true ise zip dosyasını taşır.*/
                            zipIslm.zipTasima(zipDosyaAdi,kaynakDizin,hedefDizin);
                            islem.dosyaTasima(secilenDosyalar,hedefDizin);
                            JOptionPane.showMessageDialog(this, "Tüm Dosyaları ve zipi taşıma başarılı!");
                            zipButtonKontrolTum=false;
                            /*Ziplenmiş dosyayı taşıdıktan zipKontrolButton değişkenine false atanır.
                            (zipKontrolButton true kalırsa sadece zip dosyasını taşır, ardından başka bir taşıma işlemi yapmaz.)*/
                        }
                        else {
                            /Eğer zipKontrolButton değişkeni true değilse yalnızca türü seçilen dosyaları taşır./
                            islem.dosyaTasima(secilenDosyalar,hedefDizin);
                            JOptionPane.showMessageDialog(this, "Tüm Dosyaları taşıma başarılı!");
                        }
                    }

                    else {
                        JOptionPane.showMessageDialog(this, "Hedef konum geçerli bir dizin değil!");
                    }
                }
                else {
                    JOptionPane.showMessageDialog(this, "Kaynak dizin geçerli değil!");
                }
            }
        }

        else if (pdf.isSelected()) {
            secilenDosyalar = tip.dosyaTipiSecme(kaynakDizin,".pdf");

            if (e.getActionCommand().equals("Şifrele")) {
                try {
                    islem.dosyaSifreleme(secilenDosyalar);
                    JOptionPane.showMessageDialog(this, "Pdf Dosyalar Şifrelendi");
                } catch (IOException | IllegalBlockSizeException | BadPaddingException |
                         NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException ex) {
                    throw new RuntimeException(ex);
                }
            }

            else if (e.getActionCommand().equals("Şifreyi Çöz")) {
                try {
                    islem.sifreCoz(secilenDosyalar);
                    JOptionPane.showMessageDialog(this, "Şifre çözümlendi.");
                } catch (IOException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException |
                         BadPaddingException | IllegalBlockSizeException ex) {
                    throw new RuntimeException(ex);
                }
            }

            else if (e.getActionCommand().equals("Sıkıştır")) {
                zipDosyaAdi = JOptionPane.showInputDialog(this,
                        "Oluşturmak istediğiniz zip adını sonuna '.zip' ifadesini ekleyerek giriniz:");

                if (zipDosyaAdi != null && !zipDosyaAdi.isEmpty()) {
                    zipButtonKontrolPdf = true;
                    zipIslm.dosyaZipleme(zipDosyaAdi, secilenDosyalar, kaynakDizin);
                    JOptionPane.showMessageDialog(this, "Pdf Dosyalar Sıkıştırıldı");
                }
            }

            else if (e.getActionCommand().equals("Gizle")) {
                try {
                    islem.dosyaGizleme(secilenDosyalar);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                JOptionPane.showMessageDialog(this, "PDF Dosyaları Gizlendi");
            }

            else if (e.getActionCommand().equals("Taşı")) {
                if (kaynak.exists() && kaynak.isDirectory()) {
                    if (hedef.exists() && hedef.isDirectory()) {

                        if(zipButtonKontrolPdf) {
                            zipIslm.zipTasima(zipDosyaAdi,kaynakDizin,hedefDizin);
                            islem.dosyaTasima(secilenDosyalar,hedefDizin);
                            JOptionPane.showMessageDialog(this, "PDF Dosyaları ve zipi taşıma başarılı!");
                            zipButtonKontrolPdf=false;
                        }
                        else {
                            islem.dosyaTasima(secilenDosyalar,hedefDizin);
                            JOptionPane.showMessageDialog(this, "PDF Dosyaları taşıma başarılı!");
                        }
                    }

                    else {
                        JOptionPane.showMessageDialog(this, "Hedef konum geçerli bir dizin değil!");
                    }
                }
                else {
                    JOptionPane.showMessageDialog(this, "Kaynak dizin geçerli değil!");
                }
            }
        }

        else if (doc.isSelected()) {
            secilenDosyalar = tip.dosyaTipiSecme(kaynakDizin,".docx");

            if (e.getActionCommand().equals("Şifrele")) {
                try {
                    islem.dosyaSifreleme(secilenDosyalar);
                    JOptionPane.showMessageDialog(this, "Doc Dosyalar Şifrelendi");
                } catch (IOException | IllegalBlockSizeException | BadPaddingException |
                         NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException ex) {
                    throw new RuntimeException(ex);
                }
            }

            else if (e.getActionCommand().equals("Şifreyi Çöz")) {
                try {
                    islem.sifreCoz(secilenDosyalar);
                    JOptionPane.showMessageDialog(this, "Şifre çözümlendi.");
                } catch (IOException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException |
                         BadPaddingException | IllegalBlockSizeException ex) {
                    throw new RuntimeException(ex);
                }
            }

            else if (e.getActionCommand().equals("Sıkıştır")) {
                zipDosyaAdi = JOptionPane.showInputDialog(this,
                        "Oluşturmak istediğiniz zip adını sonuna '.zip' ifadesini ekleyerek giriniz:");

                if (zipDosyaAdi != null && !zipDosyaAdi.isEmpty()) {
                    zipButtonKontrolDoc = true;
                    zipIslm.dosyaZipleme(zipDosyaAdi, secilenDosyalar, kaynakDizin);
                    JOptionPane.showMessageDialog(this, "Doc Dosyalar Sıkıştırıldı");
                }
            }

            else if (e.getActionCommand().equals("Gizle")) {
                try {
                    islem.dosyaGizleme(secilenDosyalar);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                JOptionPane.showMessageDialog(this, "DOC Dosyaları Gizlendi");
            }

            else if (e.getActionCommand().equals("Taşı")) {
                if (kaynak.exists() && kaynak.isDirectory()) {
                    if (hedef.exists() && hedef.isDirectory()) {

                        if(zipButtonKontrolDoc) {
                            zipIslm.zipTasima(zipDosyaAdi,kaynakDizin,hedefDizin);
                            islem.dosyaTasima(secilenDosyalar,hedefDizin);
                            JOptionPane.showMessageDialog(this, "DOC Dosyaları ve zipi taşıma başarılı!");
                            zipButtonKontrolDoc=false;
                        }
                        else {
                            islem.dosyaTasima(secilenDosyalar,hedefDizin);
                            JOptionPane.showMessageDialog(this, "DOC Dosyaları taşıma başarılı!");
                        }
                    }

                    else {
                        JOptionPane.showMessageDialog(this, "Hedef konum geçerli bir dizin değil!");
                    }
                }
                else {
                    JOptionPane.showMessageDialog(this, "Kaynak dizin geçerli değil!");
                }
            }
        }

        else if (txt.isSelected()) {
            secilenDosyalar = tip.dosyaTipiSecme(kaynakDizin,".txt");

            if (e.getActionCommand().equals("Şifrele")) {
                try {
                    islem.dosyaSifreleme(secilenDosyalar);
                    JOptionPane.showMessageDialog(this, "Txt Dosyalar Şifrelendi");
                } catch (IOException | IllegalBlockSizeException | BadPaddingException |
                         NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException ex) {
                    throw new RuntimeException(ex);
                }
            }

            else if (e.getActionCommand().equals("Şifreyi Çöz")) {
                try {
                    islem.sifreCoz(secilenDosyalar);
                    JOptionPane.showMessageDialog(this, "Şifre çözümlendi.");
                } catch (IOException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException |
                         BadPaddingException | IllegalBlockSizeException ex) {
                    throw new RuntimeException(ex);
                }
            }

            else if (e.getActionCommand().equals("Sıkıştır")) {
                zipDosyaAdi = JOptionPane.showInputDialog(this,
                        "Oluşturmak istediğiniz zip adını sonuna '.zip' ifadesini ekleyerek giriniz:");

                if (zipDosyaAdi != null && !zipDosyaAdi.isEmpty()) {
                    zipButtonKontrolTxt = true;
                    zipIslm.dosyaZipleme(zipDosyaAdi, secilenDosyalar, kaynakDizin);
                    JOptionPane.showMessageDialog(this, "Txt Dosyalar Sıkıştırıldı");
                }
            }

            else if (e.getActionCommand().equals("Gizle")) {
                try {
                    islem.dosyaGizleme(secilenDosyalar);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                JOptionPane.showMessageDialog(this, "TXT Dosyaları Gizlendi");
            }

            else if (e.getActionCommand().equals("Taşı")) {
                if (kaynak.exists() && kaynak.isDirectory()) {
                    if (hedef.exists() && hedef.isDirectory()) {

                        if(zipButtonKontrolTxt) {
                            zipIslm.zipTasima(zipDosyaAdi,kaynakDizin,hedefDizin);
                            islem.dosyaTasima(secilenDosyalar,hedefDizin);
                            JOptionPane.showMessageDialog(this, "TXT Dosyaları ve zipi taşıma başarılı!");
                            zipButtonKontrolTxt=false;
                        }
                        else {
                            islem.dosyaTasima(secilenDosyalar,hedefDizin);
                            JOptionPane.showMessageDialog(this, "TXT Dosyaları taşıma başarılı!");
                        }
                    }
                    else {
                        JOptionPane.showMessageDialog(this, "Hedef konum geçerli bir dizin değil!");
                    }
                }
                else {
                    JOptionPane.showMessageDialog(this, "Kaynak dizin geçerli değil!");
                }
            }
        }

        else if (png.isSelected()) {
            secilenDosyalar = tip.dosyaTipiSecme(kaynakDizin,".png");

            if (e.getActionCommand().equals("Şifrele")) {
                try {
                    islem.dosyaSifreleme(secilenDosyalar);
                    JOptionPane.showMessageDialog(this, "Png Dosyalar Şifrelendi");
                } catch (IOException | IllegalBlockSizeException | BadPaddingException |
                         NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException ex) {
                    throw new RuntimeException(ex);
                }
            }

            else if (e.getActionCommand().equals("Şifreyi Çöz")) {
                try {
                    islem.sifreCoz(secilenDosyalar);
                    JOptionPane.showMessageDialog(this, "Şifre çözümlendi.");
                } catch (IOException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException |
                         BadPaddingException | IllegalBlockSizeException ex) {
                    throw new RuntimeException(ex);
                }
            }

            else if (e.getActionCommand().equals("Sıkıştır")) {
                zipDosyaAdi = JOptionPane.showInputDialog(this,
                        "Oluşturmak istediğiniz zip adını sonuna '.zip' ifadesini ekleyerek giriniz:");

                if (zipDosyaAdi != null && !zipDosyaAdi.isEmpty()) {
                    zipButtonKontrolPng = true;
                    zipIslm.dosyaZipleme(zipDosyaAdi, secilenDosyalar, kaynakDizin);
                    JOptionPane.showMessageDialog(this, "Png Dosyalar Sıkıştırıldı");
                }
            }

            else if (e.getActionCommand().equals("Gizle")) {
                try {
                    islem.dosyaGizleme(secilenDosyalar);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                JOptionPane.showMessageDialog(this, "PNG Dosyaları Gizlendi");
            }

            else if (e.getActionCommand().equals("Taşı")) {
                if (kaynak.exists() && kaynak.isDirectory()) {
                    if (hedef.exists() && hedef.isDirectory()) {

                        if(zipButtonKontrolPng) {
                            zipIslm.zipTasima(zipDosyaAdi,kaynakDizin,hedefDizin);
                            islem.dosyaTasima(secilenDosyalar,hedefDizin);
                            JOptionPane.showMessageDialog(this, "PNG Dosyaları ve zipi taşıma başarılı!");
                            zipButtonKontrolPng=false;
                        }
                        else {
                            islem.dosyaTasima(secilenDosyalar,hedefDizin);
                            JOptionPane.showMessageDialog(this, "PNG Dosyaları taşıma başarılı!");
                        }
                    }

                    else {
                        JOptionPane.showMessageDialog(this, "Hedef konum geçerli bir dizin değil!");
                    }
                }
                else {
                    JOptionPane.showMessageDialog(this, "Kaynak dizin geçerli değil!");
                }
            }
        }
        else {
            JOptionPane.showMessageDialog(this, "Dosya türü seçin");
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Main2();
            }
        });
    }
}
