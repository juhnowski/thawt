import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Thaw {
    public static void main(String[] args) {
        read("weather.csv");
    }

    public static void read(String file)
    {
        try {
            FileReader filereader = new FileReader(file);

            CSVParser parser = new CSVParserBuilder().withSeparator(';').build();

            CSVReader csvReader = new CSVReaderBuilder(filereader)
                    .withCSVParser(parser)
                    .build();

            List<String[]> allData = csvReader.readAll();

            boolean Semenov_ust_period = false;
            boolean Rustay_ust_period = false;

            int Semenov_count_frosty_days = 0;
            int Rustay_count_frosty_days = 0;

            ArrayList<String> Semenov_thaws = new ArrayList<>();
            ArrayList<String> Rustay_thaws = new ArrayList<>();

            for (String[] data : allData) {
                    float Semenov_sr = Float.parseFloat(data[1].replace(",", "."));
                    float Semenov_max = Float.parseFloat(data[2].replace(",", "."));
                    float Semenov_min = Float.parseFloat(data[4].replace(",", "."));

                    float Rustay_max;
                    if (data[3].equals("-")) {
                        Rustay_max = Semenov_max;
                    } else {
                        Rustay_max = Float.parseFloat(data[3].replace(",", "."));
                    }

                    float Rustay_min;
                    if (data[5].equals("-")) {
                        Rustay_min = Semenov_min;
                    } else {
                        Rustay_min = Float.parseFloat(data[5].replace(",", "."));
                    }

                    float Rustay_sr = (Rustay_max-Rustay_min)/2;

                    /*
повышение максимальной температуры воздуха до 0 и выше
внутри устойчивого морозного периода
начало - максимум отрицательный 5 дней
конец переход среднесуточной температуры через 0
                     */
                    if (Semenov_ust_period) {
                        if (Semenov_sr >= 0) {
                            Semenov_ust_period = false;
                            Semenov_count_frosty_days = 0;
                        } else {
                            if (Semenov_max < 0) {
                                Semenov_thaws.add(data[0]);
                                System.out.println("Семенов " + data[0] + " оттепель" );
                            }
                        }
                    } else {
                        if (Semenov_max < 0) {
                            if (++Semenov_count_frosty_days>4){
                                Semenov_ust_period = true;
                                Semenov_thaws.add(data[0]);
                                System.out.println("Семенов " + data[0] + " оттепель" );
                            };
                        } else {
                            Semenov_count_frosty_days = 0;
                        }
                    }
// Рустай--------------------------------------------------------------------------------
                    if (Rustay_ust_period) {
                        if (Rustay_sr >= 0) {
                            Rustay_ust_period = false;
                            Rustay_count_frosty_days = 0;
                        } else {
                            if (Rustay_max < 0) {
                                Rustay_thaws.add(data[0]);
                                System.out.println("Рустай " + data[0] + " оттепель" );
                            }
                        }
                    } else {
                        if (Rustay_max < 0) {
                            if (++Rustay_count_frosty_days>4){
                                Rustay_ust_period = true;
                                Rustay_thaws.add(data[0]);
                                System.out.println("Рустай " + data[0] + " оттепель" );
                            };
                        } else {
                            Rustay_count_frosty_days = 0;
                        }
                    }
                }
            System.out.println("Итого");
            System.out.println("Семенов: " + Semenov_thaws.size() + " дней");
            System.out.println("Рустай: " + Rustay_thaws.size() + " дней");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
