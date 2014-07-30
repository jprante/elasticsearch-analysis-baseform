# Elasticsearch Analysis Baseform Plugin

Baseform is an analysis plugin for [Elasticsearch](http://github.com/elasticsearch/elasticsearch).

With the baseform analysis, you can use a token filter for reducing word forms to their base form.

Currently, only baseforms for german and english are implemented.

Example: the german base form of `zurückgezogen` is `zurückziehen`.

## Versions

| Elasticsearch version    | Plugin      | Release date |
| ------------------------ | ----------- | -------------|
| 1.3.0                    | 1.3.0.0     | Jul 30, 2014 |


## Installation

    ./bin/plugin -install support -url http://xbib.org/repository/org/xbib/elasticsearch/plugin/elasticsearch-analysis-baseform/1.3.0.0/elasticsearch-analysis-baseform-1.3.0.0-plugin.zip

Do not forget to restart the node after installing.

## Checksum

| File                                                | SHA1                                     |
| --------------------------------------------------- | -----------------------------------------|
| elasticsearch-analysis-baseform-1.3.0.0-plugin.zip  | b4010969b6b442302b5b2c59718f83352b6e8ec4 |

Do not forget to restart the node after installing.

## Project docs

The Maven project site is available at [Github](http://jprante.github.io/elasticsearch-analysis-baseform)

## Issues

All feedback is welcome! If you find issues, please post them at [Github](https://github.com/jprante/elasticsearch-analysis-baseform/issues)

## Example (german)

In the settings, set up a token filter of type "baseform" and language "de"::

    {
     "index":{
        "analysis":{
            "filter":{
                "baseform":{
                    "type" : "baseform",
                    "language" : "de"
                }
            },
            "tokenizer" : {
                "baseform" : {
                   "type" : "standard",
                   "filter" : [ "baseform" ]
                }
            }
        }
     }
    }

By using such a tokenizer, the sentence
"Die Jahresfeier der Rechtsanwaltskanzleien auf dem Donaudampfschiff hat viel Ökosteuer gekostet"
will be tokenized into
"Die", "Die", "Jahresfeier", "Jahresfeier", "der", "der", "Rechtsanwaltskanzleien", "Rechtsanwaltskanzlei",
"auf", "auf", "dem", "der", "Donaudampfschiff", "Donaudampfschiff", "hat", "haben", "viel", "viel",
"Ökosteuer", "Ökosteuer", "gekostet", "kosten"

It is recommended to add the [Unique token filter](http://www.elasticsearch.org/guide/reference/index-modules/analysis/unique-tokenfilter.html) to skip tokens that occur more than once.

## Example (english)

In the settings, given this token filter of type "baseform" and language "en" has been set up::


    {
       "index" : {
          "analysis" : {
              "filter" : {
                  "baseform" : {
                      "type" : "baseform",
                      "language" : "en"
                  }
              },
              "analyzer" : {
                  "baseform" : {
                      "tokenizer" : "standard",
                      "filter" : [ "baseform", "unique" ]
                  }
              }
          }
       }
    }


Then, with the text::

    “I have a dream that one day this nation will rise up, and live out the true meaning of its creed: ‘We hold these truths to be self-evident: that all men are created equal.’
    I have a dream that one day on the red hills of Georgia the sons of former slaves and the sons of former slave owners will be able to sit down together at a table of brotherhood.
    I have a dream that one day even the state of Mississippi, a state sweltering with the heat of injustice and sweltering with the heat of oppression, will be transformed into an oasis of freedom and justice.
    I have a dream that my four little children will one day live in a nation where they will not be judged by the color of their skin but by the content of their character.
    I have a dream today!”

this token stream will be produced::

    "I","have","a","dream","that","one","day","this","nation","will","rise","up","and","live","out",
    "the","true","meaning","mean","of","its","creed","We","hold","these","truths","truth","to","be",
    "self","evident","all","men","man","are","created","create","equal","on","red","hills","hill",
    "Georgia","sons","son","former","slaves","slave","owners","owner","able","sit","down","together",
    "at","table","brotherhood","even","state","Mississippi","sweltering","swelter","with","heat",
    "injustice","oppression","transformed","transform","into","an","oasis","freedom","justice","my",
    "four","little","children","child","in","where","they","not","judged","judge","by","color","their",
    "skin","but","content","character","today"

As an alternative, separate dictionaries for `en-verbs` and `en-nouns` are available.

# License

Elasticsearch Baseform Analysis Plugin

Copyright (C) 2013 Jörg Prante

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

# Credits

The FSA for compiling the fullform/baseform table is taken from Dawid Weiss' morfologik project

https://github.com/morfologik/morfologik-stemming

The german baseform file is a modified version of Daniel Nabers morphology file

http://www.danielnaber.de/morphologie/morphy-mapping-20110717.latin1.gz

and is distributed under CC-BY-SA http://creativecommons.org/licenses/by-sa/3.0/

The english baseforms are a modified version of the english.dict file
of http://languagetool.org/download/snapshots/LanguageTool-20131115-snapshot.zip
which is licensed under LGPL http://www.fsf.org/licensing/licenses/lgpl.html#SEC1
