
package com.crio.warmup.stock.portfolio;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerImpl implements PortfolioManager {
  String APIKEY = "d08f8029dbcff3847be41854b53b61dbd1471e6c";
  private RestTemplate restTemplate;

public PortfolioManagerImpl(RestTemplate restTemplate){
  this.restTemplate=restTemplate;
}

  // TODO: CRIO_TASK_MODULE_REFACTOR
  // 1. Now we want to convert our code into a module, so we will not call it from main anymore.
  // Copy your code from Module#3 PortfolioManagerApplication#calculateAnnualizedReturn
  // into #calculateAnnualizedReturn function here and ensure it follows the method signature.
  // 2. Logic to read Json file and convert them into Objects will not be required further as our
  // clients will take care of it, going forward.

  // Note:
  // Make sure to exercise the tests inside PortfolioManagerTest using command below:
  // ./gradlew test --tests PortfolioManagerTest

 

  // CHECKSTYLE:OFF
  static Double getOpeningPriceOnStartDate(List<Candle> candles) {
    if (candles == null || candles.isEmpty()) {
      return null;
    }
    return candles.get(0).getOpen();
  }


  public static Double getClosingPriceOnEndDate(List<Candle> candles) {
    if (candles == null || candles.isEmpty()) {
      return null;
    }
    return candles.get(candles.size() - 1).getClose();
  }


//   public List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades, LocalDate endDate) {
//     List<AnnualizedReturn> annualizedReturns = new ArrayList<>();

//     for (PortfolioTrade trade : portfolioTrades) {
//         try {
//             List<Candle> candles = null;
//             candles = getStockQuote(trade.getSymbol(), trade.getPurchaseDate(), endDate);
//             Double buyPrice = getOpeningPriceOnStartDate(candles);
//             Double sellPrice = getClosingPriceOnEndDate(candles);
//             double totalReturns = (sellPrice - buyPrice) / buyPrice;
//             long daysBetween = ChronoUnit.DAYS.between(trade.getPurchaseDate(), endDate);
//             double totalNumYears = daysBetween / 365.0;
//             double annualizedReturn = Math.pow(1 + totalReturns, 1 / totalNumYears) - 1;
//             AnnualizedReturn annualizedReturnObj = new AnnualizedReturn(trade.getSymbol(), annualizedReturn, totalReturns);
//             annualizedReturns.add(annualizedReturnObj);
//         } catch (JsonProcessingException e) {
//             e.printStackTrace();
//         }
//     }

//     // Sort annualizedReturns in descending order by annualizedReturn field using the getComparator() method
//     annualizedReturns.sort(getComparator());

//     return annualizedReturns;
// }


  // public static Double getClosingPriceOnEndDate(List<Candle> candles) {
  //   if (candles == null || candles.isEmpty()) {
  //     return null;
  //   }
  //   return candles.get(candles.size() - 1).getClose();
  // }


  public List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades, LocalDate endDate) {
    List<AnnualizedReturn> annualizedReturns = new ArrayList<>();

    for (PortfolioTrade trade : portfolioTrades) {
        try {
            List<Candle> candles = null;
            candles = getStockQuote(trade.getSymbol(), trade.getPurchaseDate(), endDate);
            Double buyPrice = getOpeningPriceOnStartDate(candles);
            Double sellPrice = getClosingPriceOnEndDate(candles);
            double totalReturns = (sellPrice - buyPrice) / buyPrice;
            long daysBetween = ChronoUnit.DAYS.between(trade.getPurchaseDate(), endDate);
            double totalNumYears = daysBetween / 365.0;
            double annualizedReturn = Math.pow(1 + totalReturns, 1 / totalNumYears) - 1;
            AnnualizedReturn annualizedReturnObj = new AnnualizedReturn(trade.getSymbol(), annualizedReturn, totalReturns);
            annualizedReturns.add(annualizedReturnObj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    // Sort annualizedReturns in descending order by annualizedReturn field using the getComparator() method
    annualizedReturns.sort(getComparator());

    return annualizedReturns;
}

   private Comparator<AnnualizedReturn> getComparator() {
    return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  }

  // CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_REFACTOR
  // Extract the logic to call Tiingo third-party APIs to a separate function.
  // Remember to fill out the buildUri function and use that.


  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to) throws JsonProcessingException {
    // Build the Tiingo API endpoint URL
    ObjectMapper mapper = getObjectMapper();
    //  RestTemplate restTemplate = new RestTemplate();
    if (to.isBefore(from)) {
        to = LocalDate.now();
    }
    String url = buildUri(symbol, from, to);
    System.out.println(url);
    String result = restTemplate.getForObject(url, String.class);
    System.out.println(result);
    return Arrays.asList(mapper.readValue(result, TiingoCandle[].class));
}


private ObjectMapper getObjectMapper() {
  ObjectMapper objectMapper = new ObjectMapper();
  objectMapper.registerModule(new JavaTimeModule());
  return objectMapper;
}



  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
    String token = "d08f8029dbcff3847be41854b53b61dbd1471e6c";
    String url = "https://api.tiingo.com/tiingo/daily/" + symbol + "/prices?startDate="
    + startDate + "&endDate=" + endDate + "&token=" + token;

   return url;


  }
}
