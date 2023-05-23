
package com.crio.warmup.stock;


import com.crio.warmup.stock.dto.*;
import com.crio.warmup.stock.log.UncaughtExceptionHandler;
import com.crio.warmup.stock.portfolio.PortfolioManager;
import com.crio.warmup.stock.portfolio.PortfolioManagerFactory;
import com.crio.warmup.stock.portfolio.PortfolioManagerImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import java.util.UUID;
import java.util.logging.Logger;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.web.client.RestTemplate;


public class PortfolioManagerApplication {
  String tiingoToken = "d08f8029dbcff3847be41854b53b61dbd1471e6c";
  

  // TODO: CRIO_TASK_MODULE_JSON_PARSING
  // Task:
  // - Read the json file provided in the argument[0], The file is available in the classpath.
  // - Go through all of the trades in the given file,
  // - Prepare the list of all symbols a portfolio has.
  // - if "trades.json" has trades like
  // [{ "symbol": "MSFT"}, { "symbol": "AAPL"}, { "symbol": "GOOGL"}]
  // Then you should return ["MSFT", "AAPL", "GOOGL"]
  // Hints:
  // 1. Go through two functions provided - #resolveFileFromResources() and #getObjectMapper
  // Check if they are of any help to you.
  // 2. Return the list of all symbols in the same order as provided in json.

  // Note:
  // 1. There can be few unused imports, you will need to fix them to make the build pass.
  // 2. You can use "./gradlew build" to check if your code builds successfully.

  public static List<String> mainReadFile(String[] args) throws IOException, URISyntaxException {
    File file = resolveFileFromResources(args[0]);
    ObjectMapper objectMapper = getObjectMapper();
    List<PortfolioTrade> trades =
        objectMapper.readValue(file, new TypeReference<List<PortfolioTrade>>() {});
    List<String> symbols = new ArrayList<>();
    for (PortfolioTrade trade : trades) {
      symbols.add(trade.getSymbol());
    }
    return symbols;
  }


  // Note:
  // 1. You may need to copy relevant code from #mainReadQuotes to parse the Json.
  // 2. Remember to get the latest quotes from Tiingo API.



  // Note:
  // 1. You may have to register on Tiingo to get the api_token.
  // 2. Look at args parameter and the module instructions carefully.
  // 2. You can copy relevant code from #mainReadFile to parse the Json.
  // 3. Use RestTemplate#getForObject in order to call the API,
  // and deserialize the results in List<Candle>



  private static void printJsonObject(Object object) throws IOException {
    Logger logger = Logger.getLogger(PortfolioManagerApplication.class.getCanonicalName());
    ObjectMapper mapper = new ObjectMapper();
    logger.info(mapper.writeValueAsString(object));
  }

  private static File resolveFileFromResources(String filename) throws URISyntaxException {
    return Paths.get(Thread.currentThread().getContextClassLoader().getResource(filename).toURI())
        .toFile();
  }

  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }


  // TODO: CRIO_TASK_MODULE_JSON_PARSING
  // Follow the instructions provided in the task documentation and fill up the correct values for
  // the variables provided. First value is provided for your reference.
  // A. Put a breakpoint on the first line inside mainReadFile() which says
  // return Collections.emptyList();
  // B. Then Debug the test #mainReadFile provided in PortfoliomanagerApplicationTest.java
  // following the instructions to run the test.
  // Once you are able to run the test, perform following tasks and record the output as a
  // String in the function below.
  // Use this link to see how to evaluate expressions -
  // https://code.visualstudio.com/docs/editor/debugging#_data-inspection
  // 1. evaluate the value of "args[0]" and set the value
  // to the variable named valueOfArgument0 (This is implemented for your reference.)
  // 2. In the same window, evaluate the value of expression below and set it
  // to resultOfResolveFilePathArgs0
  // expression ==> resolveFileFromResources(args[0])
  // 3. In the same window, evaluate the value of expression below and set it
  // to toStringOfObjectMapper.
  // You might see some garbage numbers in the output. Dont worry, its expected.
  // expression ==> getObjectMapper().toString()
  // 4. Now Go to the debug window and open stack trace. Put the name of the function you see at
  // second place from top to variable functionNameFromTestFileInStackTrace
  // 5. In the same window, you will see the line number of the function in the stack trace window.
  // assign the same to lineNumberFromTestFileInStackTrace
  // Once you are done with above, just run the corresponding test and
  // make sure its working as expected. use below command to do the same.
  // ./gradlew test --tests PortfolioManagerApplicationTest.testDebugValues
  public static List<String> debugOutputs() {

    String valueOfArgument0 = "trades.json";
    String resultOfResolveFilePathArgs0 = "trades.json";
    String toStringOfObjectMapper = "ObjectMapper";
    String functionNameFromTestFileInStackTrace = "mainReadFile";
    String lineNumberFromTestFileInStackTrace = "17";


    return Arrays.asList(
        new String[] {valueOfArgument0, resultOfResolveFilePathArgs0, toStringOfObjectMapper,
            functionNameFromTestFileInStackTrace, lineNumberFromTestFileInStackTrace});
  }



  public static List<PortfolioTrade> readTradesFromJson(String filename)
      throws IOException, URISyntaxException {
    List<PortfolioTrade> portfolioTrades = getObjectMapper().readValue(
        resolveFileFromResources(filename), new TypeReference<List<PortfolioTrade>>() {});
    return portfolioTrades;
  }



  public static String prepareUrl(PortfolioTrade trade, LocalDate endDate, String token) {
    return "https://api.tiingo.com/tiingo/daily/" + trade.getSymbol() + "/prices?startDate="
        + trade.getPurchaseDate() + "&endDate=" + endDate + "&token=" + token;
  }


  public static List<String> mainReadQuotes(String[] args) throws IOException, URISyntaxException {
    final String tiingoToken = "d08f8029dbcff3847be41854b53b61dbd1471e6c";
    List<PortfolioTrade> portfolioTrades = readTradesFromJson(args[0]);
    LocalDate endDate = LocalDate.parse(args[1]);
    RestTemplate restTemplate = new RestTemplate();
    List<TotalReturnsDto> totalReturnsDtos = new ArrayList<>();
    List<String> listofSortedSymbolOnCLosingPrice = new ArrayList<>();
    for (PortfolioTrade portfolioTrade : portfolioTrades) {
      String tiingoURL = prepareUrl(portfolioTrade, endDate, tiingoToken);
      TiingoCandle[] tiingoCandlesArray =
          restTemplate.getForObject(tiingoURL, TiingoCandle[].class);
      totalReturnsDtos.add(new TotalReturnsDto(portfolioTrade.getSymbol(),
          tiingoCandlesArray[tiingoCandlesArray.length - 1].getClose()));
    }

    // Sorting
    Collections.sort(totalReturnsDtos,
        (a, b) -> Double.compare(a.getClosingPrice(), b.getClosingPrice()));
    for (TotalReturnsDto totalReturnsDto : totalReturnsDtos) {
      listofSortedSymbolOnCLosingPrice.add(totalReturnsDto.getSymbol());
    }
    return listofSortedSymbolOnCLosingPrice;

    // Read the file name and date from the command line arguments


  }


  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  // Now that you have the list of PortfolioTrade and their data, calculate annualized returns
  // for the stocks provided in the Json.
  // Use the function you just wrote #calculateAnnualizedReturns.
  // Return the list of AnnualizedReturns sorted by annualizedReturns in descending order.

  // Note:
  // 1. You may need to copy relevant code from #mainReadQuotes to parse the Json.
  // 2. Remember to get the latest quotes from Tiingo API.



  // TODO:
  // Ensure all tests are passing using below command
  // ./gradlew test --tests ModuleThreeRefactorTest
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


  public static List<Candle> fetchCandles(PortfolioTrade trade, LocalDate endDate, String token)
      throws JsonProcessingException {

    String apiUrl = String.format(
        "https://api.tiingo.com/tiingo/daily/%s/prices?startDate=%s&endDate=%s&token=%s",
        trade.getSymbol(), trade.getPurchaseDate(), endDate, token);

    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    String response = new RestTemplate().getForObject(apiUrl, String.class);

    Candle[] result = objectMapper.readValue(response, TiingoCandle[].class);


    return Arrays.asList(result);
  }



  public static List<AnnualizedReturn> mainCalculateSingleReturn(String[] args)
      throws IOException, URISyntaxException {
    if (args.length != 2) {
      throw new IllegalArgumentException("Two arguments expected: filename and date");
    }
    String filename = args[0];
    LocalDate date = LocalDate.parse(args[1]);

    List<PortfolioTrade> trades = readTradesFromJson(filename);

    List<AnnualizedReturn> annualizedReturns = new ArrayList<>();
    for (PortfolioTrade trade : trades) {
      List<Candle> candles = null;

      candles = fetchCandles(trade, date, getToken());

      if (candles != null && !candles.isEmpty()) {
        Double buyPrice = getOpeningPriceOnStartDate(candles);
        Double sellPrice = getClosingPriceOnEndDate(candles);
        AnnualizedReturn annualizedReturn =
            calculateAnnualizedReturns(date, trade, buyPrice, sellPrice);
        annualizedReturns.add(annualizedReturn);
      }
    }
    Collections.sort(annualizedReturns);
    return annualizedReturns;
  }


  // public static List<AnnualizedReturn> mainCalculateSingleReturn(String[] args)
  // throws IOException, URISyntaxException {
  // if (args.length != 2) {
  // throw new IllegalArgumentException("Two arguments expected: filename and date");
  // }
  // String filename = args[0];
  // LocalDate date = LocalDate.parse(args[1]);

  // List<PortfolioTrade> trades = readTradesFromJson(filename);

  // List<AnnualizedReturn> annualizedReturns = new ArrayList<>();
  // for (PortfolioTrade trade : trades) {
  // // Check if endDate is after purchase date of trade
  // if (date.isBefore(trade.getPurchaseDate())) {
  // System.out.println("Error: End date is before purchase date for trade: " + trade.getSymbol());
  // continue;
  // }
  // List<Candle> candles = null;
  // try {
  // candles = fetchCandles(trade, date, getToken());
  // } catch (FileNotFoundException e) {
  // throw new RuntimeException("File not found: " + filename, e);
  // } catch (Exception e) {
  // System.out.println("Error while fetching candles for trade: " + trade.getSymbol() + " - " +
  // e.getMessage());
  // }
  // if (candles != null && !candles.isEmpty()) {
  // Double buyPrice = getOpeningPriceOnStartDate(candles);
  // Double sellPrice = getClosingPriceOnEndDate(candles);
  // AnnualizedReturn annualizedReturn = calculateAnnualizedReturns(date, trade, buyPrice,
  // sellPrice);
  // annualizedReturns.add(annualizedReturn);
  // }
  // }
  // return annualizedReturns;
  // }



  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  // Return the populated list of AnnualizedReturn for all stocks.
  // Annualized returns should be calculated in two steps:
  // 1. Calculate totalReturn = (sell_value - buy_value) / buy_value.
  // 1.1 Store the same as totalReturns
  // 2. Calculate extrapolated annualized returns by scaling the same in years span.
  // The formula is:
  // annualized_returns = (1 + total_returns) ^ (1 / total_num_years) - 1
  // 2.1 Store the same as annualized_returns
  // Test the same using below specified command. The build should be successful.
  // ./gradlew test --tests PortfolioManagerApplicationTest.testCalculateAnnualizedReturn

  public static AnnualizedReturn calculateAnnualizedReturns(LocalDate endDate, PortfolioTrade trade,
     Double buyPrice, Double sellPrice) {
    Double totalReturns = (sellPrice - buyPrice) / buyPrice;
    long daysBetween = ChronoUnit.DAYS.between(trade.getPurchaseDate(), endDate);
    Double totalNumYears = daysBetween / 365.0;
    Double annualizedReturns = Math.pow(1 + totalReturns, 1 / totalNumYears) - 1;
    AnnualizedReturn annualizedReturn =
        new AnnualizedReturn(trade.getSymbol(), annualizedReturns, totalReturns);
    return annualizedReturn;
  }



  public static String getToken() {
    return "d08f8029dbcff3847be41854b53b61dbd1471e6c";
  }



  // TODO: CRIO_TASK_MODULE_REFACTOR
  public static List<AnnualizedReturn> mainCalculateReturnsAfterRefactor(String[] args)
      throws Exception {
    String file = args[0];
    LocalDate endDate = LocalDate.parse(args[1]);
    String contents = readFileAsString(file);
    ObjectMapper objectMapper = getObjectMapper();
    List<PortfolioTrade> portfolioTrades =
        objectMapper.readValue(contents, new TypeReference<List<PortfolioTrade>>() {});
    PortfolioManager portfolioManager = PortfolioManagerFactory.getPortfolioManager(null);
    return portfolioManager.calculateAnnualizedReturn(portfolioTrades, endDate);
  }


  private static String readFileAsString(String file) throws IOException {
    return new String(Files.readAllBytes(Paths.get(file)));
  }


  public static void main(String[] args) throws Exception {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    ThreadContext.put("runId", UUID.randomUUID().toString());
    PortfolioManagerImpl portfolioManagerImpl=new PortfolioManagerImpl(new RestTemplate());
    // LocalDate srart = LocalDate.parse("2020-01-01");
    // LocalDate end = LocalDate.parse("2020-12-01");
    //portfolioManagerImpl.getStockQuote("AAPL",srart, end );

    // printJsonObject(mainReadFile(args));


    // printJsonObject(mainCalculateSingleReturn(args));

  }



}

