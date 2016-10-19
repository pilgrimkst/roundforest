#Implementation notes
* `Top N` statistics functionality implemented using [probabalistic data structures](https://highlyscalable.wordpress.com/2012/05/01/probabilistic-structures-web-analytics-data-mining/) (since we are playing in low memory profile it gives significant memory bonus for a low error rate)
* Micro batching approach was chosen also due to low memory consupmtion requirements. Small batches give better performance at cost of latency, but we don't care for it in this task.
* `Review tokenizer` was implemented naively by splitting string, and filtering english stop words. Ideally, it should be implemented based on statistical tokenizer, and, possibly reducing tokens to their normal form. It will improve accuracy for words in different forms, reduce errors for multi word terms, as well as with abbreviations. I would consider to use `Lucene`, or something like `Apache open NLP`
* It is assumed, that all reviews are in english. If it's not, we need some sort of statistical language detector, and separate parser with own stop words for each language.
* `TopKElementsAggregator` implemented `ThreadSafe`, to reduce lock waiting There are `k` internal buckets for statistics and their own locks. They are merged when statistics is requested  
* Keeping low memory footprint and preventing `OOM` is implemented using `ThreadPoolExecutor` with `BlockingQueue` and blocking stream processing when the queue is full. 
* My implementation doesn't automatically scales horizontally. If I would implement this at large scale, I would consider to use `Apache spark`, or something similar.

## Known issues:
* `CSV parser` - in the source data there is dirty unescaped rows, it breaks the input. I had used `OpenCSV` library, but it seems that I need custom implementation, that will add all text after 9 commas to the review part
*  My implementation doesn't check data for duplication. I would consider to use something like `bloom filter` from a concatenated string of `userId`, `productId`, `timestamp`, and length of `review` text. This allows to have some insights on duplicates, while keeping memory consumption less, than storing whole dataset in memory
Please create github repository and publish the code there.
* `TranslateService` is not implemented. I would implement it based on `jobs queue` optionally splitting large enough reviews by sentences(since naive splitting may affect translation meaning of merged result), then, if it still doesn't fit I would split it by words.
* I would use something like [Hystrix](https://github.com/Netflix/Hystrix) to handle errors, and timeouts
* Naming could be better
* Sorting of results is ordered by counts, not alphabetically

### To use app, you can run it with one argument, that will point to the data file ()

#### It was really fun to implement this task. Thanks for it :)



Send answers to: kuba@roundforest.com and ran@roundforest.com 
# Task (Should take 3-6 hours)

The goal of this task is to analyze and transform the > 500.000 reviews from amazon. 
Please go to Kaggle.com and download (please take the 300MB csv file): 
https://www.kaggle.com/snap/amazon-fine-food-reviews

Please note how much time did you spend doing that task. If you don’t have time to implement something just comment it so we know that you are aware of limitation. If you have questions please ask us but you can also take initiative it is enough that you comment what you decided.

Please use any JVM language. You are free to choose any frameworks and libraries that make you productive in solving the problems below.

## We are interested in:

* Finding 1000 most active users (profile names)
* Finding 1000 most commented food items (item ids).
* Finding 1000 most used words in the reviews
* We want to translate all the reviews using Google Translate API. You can send up to 1000 characters per HTTP call. API has 200ms average response time. How to do it efficiently and cost effective (you pay for API calls and we have concurrency limits -  100 requests in parallel max) - please mock the calls to google translate API. 

We assume that the endpoint will be: https://api.google.com/translate
The content type: application/json and the example body of the POST:

```
{input_lang: ‘en’, output_lang: ‘fr’, text: “Hello John, how are you?”}
```

Output:

```
{text: ‘Salut Jean, comment vas tu?’}
```

Any errors will be reported by the HTTP codes. 


* We are interested in clean testable code (add tests if you have time).
* How do you make sure that there are no duplicates in the file?
* We are interested in using full multi core CPU power.
* We will be running this on machine with 500MB of RAM. How do you make sure that we are not using more than that? How are you going to monitor the memory usage of your program?
* Our goal is to support the files with up to 100M reviews on multiple machines with 500MB of RAM and 4 core CPUs. How are you going to make it happen?

Please provide working code (command to compile and run the program) that prints output of point: 1,2,3 to standard output sorted alphabetically and executes point 4 to mocked endpoint (when launched with the argument ‘translate=true’).


