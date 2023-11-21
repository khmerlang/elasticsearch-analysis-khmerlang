# Khmerlang Analysis Plugin for Elasticsearch

Khmerlang Analysis plugin integrates Khmer language analysis into Elasticsearch.

The plugin provides `kh_analyzer` analyzer, `kh_tokenizer` tokenizer, `kh_synonym` khmer synonym, `kh_number` khmer number filter and `kh_stop` stop filter. The `kh_analyzer` is composed of the `kh_tokenizer` tokenizer, `correct_character`, `kh_number`, `stop` and `lowercase` filter.

- Re-order character: sometime words look correct however the position of characters is not. Ex “ស្រី្ត” can written as "ស +  ្ + រ +  ្ + ត + ី" or "ស +  ្ + ត +  ្ + រ + ី".
- Correct character: sometime words look correct however the character using is not correct. Ex: "ប្ដី", correct: "ប +  ្ + ដ + ី", incorrect: "ប +  ្ + ត + ី".
- Word Segmentation: build token from to segment words of input text.
- Synonyms: add synonym for some token.


## Example output

```
GET _analyze
{
  "analyzer": "kh_analyzer",
  "text": "ខ្ញុំស្រលាញ់កម្ពុជា។"
}
```

The above sentence would produce the following terms:
```
{
  "tokens" : [
    {
      "token" : "ស្រលាញ់",
      "start_offset" : 5,
      "end_offset" : 11,
      "type" : "<KH>",
      "position" : 1
    },
    {
      "token" : "កម្ពុជា",
      "start_offset" : 12,
      "end_offset" : 18,
      "type" : "<KH>",
      "position" : 2
    }
  ]
}

```

## Configuration

The `kh_analyzer` analyzer accepts the following parameters:

- `correct_character` Correct character order. Defaults to `true`.
- `khmer_number` convert khmer number to arabic. Defaults to `false`.
- `lowercase` Convert character to lowercase. Defaults to `false`.
- `dict_path` The path to tokenizer dictionary on system(TODO: not test yet).
- `keep_punctuation` Keep punctuation marks as tokens. Defaults to `false`.
- `enable_stopwords` Enable/disable stop words filter. Defaults to `false`.
- `stopwords` A pre-defined stop words list. Defaults to `stopwords.txt` file.
- `stopwords_path` The path to a file containing stop words.

### Example configuration
In this example, we configure the `kh_analyzer` analyzer to keep punctuation marks and to use the custom list of stop words:

```
PUT my-kh-index-00001
{
  "settings": {
    "analysis": {
      "analyzer": {
        "my_kh_analyzer": {
          "type": "kh_analyzer",
          "keep_punctuation": false,
          "correct_character": true,
          "lowercase": true,
          "khmer_number": true
        }
      }
    }
  }
}

GET my-kh-index-00001/_analyze
{
  "analyzer": "my_kh_analyzer",
  "text": "១២៣៤៥.៦៧អ្នកចេះ​និយាយភាសាខ្មែរទេ? 12345.67"
}
```

The above example produces the following terms:
```
{
  "tokens" : [
    {
      "token" : "12345.67",
      "start_offset" : 0,
      "end_offset" : 7,
      "type" : "<NUMBER>",
      "position" : 0
    },
    {
      "token" : "អ្នកចេះ",
      "start_offset" : 8,
      "end_offset" : 14,
      "type" : "<KH_WORD>",
      "position" : 1
    },
    {
      "token" : "និយាយ",
      "start_offset" : 15,
      "end_offset" : 19,
      "type" : "<KH_WORD>",
      "position" : 2
    },
    {
      "token" : "ភាសាខ្មែរ",
      "start_offset" : 20,
      "end_offset" : 28,
      "type" : "<KH_WORD>",
      "position" : 3
    },
    {
      "token" : "12345.67",
      "start_offset" : 34,
      "end_offset" : 41,
      "type" : "<NUMBER>",
      "position" : 5
    }
  ]
}

```

We can also create a custom analyzer with the `kh_tokenizer`. In following example, we create `my_kh_analyzer` to produce
both diacritic and no diacritic tokens in lowercase:

```
PUT my-kh-index-00002
{
  "settings": {
    "analysis": {
      "analyzer": {
        "my_kh_analyzer": {
          "tokenizer": "kh_tokenizer",
          "filter": [
            "kh_synonym",
            "kh_number",
            "kh_stop",
            "lowercase"
          ]
        }
      }
    }
  }
}

GET my-kh-index-00002/_analyze
{
  "analyzer": "my_kh_analyzer",
  "text": "១២៣៤៥.៦៧អ្នកចេះ​និយាយភាសាខ្មែរទេ? 1234"
}
```

The above example produces the following terms:
```
{
  "tokens" : [
    {
      "token" : "12345.67",
      "start_offset" : 0,
      "end_offset" : 7,
      "type" : "<NUMBER>",
      "position" : 0
    },
    {
      "token" : "អ្នកចេះ",
      "start_offset" : 8,
      "end_offset" : 14,
      "type" : "<KH_WORD>",
      "position" : 1
    },
    {
      "token" : "និយាយ",
      "start_offset" : 15,
      "end_offset" : 19,
      "type" : "<KH_WORD>",
      "position" : 2
    },
    {
      "token" : "ភាសាខ្មែរ",
      "start_offset" : 20,
      "end_offset" : 28,
      "type" : "<KH_WORD>",
      "position" : 3
    },
    {
      "token" : "1234",
      "start_offset" : 34,
      "end_offset" : 37,
      "type" : "<NUMBER>",
      "position" : 5
    }
  ]
}


```

### TODO
- word segment using deeplearning
- build synonym word list, group by category

## Use Docker

Make sure you have installed both Docker & docker-compose

### Build the image with Docker Compose

```sh
# Copy, edit ES version and password for user elastic in file .env. Default password: changeme
cp .env.sample .env
docker compose build
docker compose up
```

### Step 1: Build the plugin

Clone the plugin’s source code:

```sh
git clone git@github.com:khmerlang/elasticsearch-analysis-khmerlang.git
```

Optionally, edit the `elasticsearch-analysis-khmerlang/pom.xml` to change the version of Elasticsearch (same as plugin version) you want to build the plugin with:

```xml
...
<version>7.17.1</version>
...
 ```

Build the plugin:
```sh
cd elasticsearch-analysis-vietnamese
mvn package
```

### Step 2: Installation the plugin on Elasticsearch

```sh
bin/elasticsearch-plugin install file://target/releases/elasticsearch-analysis-khmerlang-7.17.1.zip
```

| Khmerlang Analysis Plugin  | Elasticsearch   |
|----------------------------|-----------------|
| master                     | 7.16 ~ 7.17.1   |
| 7.16.1                     | 7.16 ~ 7.17.1   |
| ...                        | ...           |


### Issues:
- Build fail due to Java version in correct. Change java version to 11: `export JAVA_HOME=$(/usr/libexec/java_home -v 11)`

### Refs:

- https://www.elastic.co/blog/multitoken-synonyms-and-graph-queries-in-elasticsearch
- https://www.elastic.co/blog/boosting-the-power-of-elasticsearch-with-synonyms
- https://www.elastic.co/guide/en/elasticsearch/plugins/8.3/analysis-kuromoji.html
- https://medium.com/@purbon/handling-similar-words-in-elasticsearch-9c80aba88627
- https://github.com/agora-team/elasticsearch-synonyms
- https://github.com/bells/elasticsearch-analysis-dynamic-synonym
- https://lucidworks.com/post/search-automatic-synonym-detection/
- https://github.com/jianfengye/elasticsearch-synonym-remote

- https://github.com/duydo/elasticsearch-analysis-vietnamese
- https://github.com/apache/lucene
- https://nlp.techostartup.center/
