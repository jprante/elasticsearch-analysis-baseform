
Elasticsearch Analysis Baseform Plugin
======================================

Baseform is an analysis plugin for `Elasticsearch <http://github.com/elasticsearch/elasticsearch>`_.

With the baseform analysis, you can use a token filter for reducing word forms to their base form.

Currently, only baseforms for german are implemented.

Example: the base form of ``zurückgezogen`` is ``zurückziehen``.

Installation
------------

Current version of the plugin is **1.0.0** (Oct 21, 2013)


Prerequisites::

  Elasticsearch 0.90.5+

=============  =========  =================  =============================================================
ES version     Plugin     Release date       Command
-------------  ---------  -----------------  -------------------------------------------------------------
0.90.5         **1.0.0**  Oct 21, 2013       ./bin/plugin --install decompound --url http://bit.ly/
=============  =========  =================  =============================================================


Example
-------

In the mapping, set up a token filter of type "baseform"::

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

It is recommended to add the `Unique token filter <http://www.elasticsearch.org/guide/reference/index-modules/analysis/unique-tokenfilter.html>`_ to skip tokens that occur more than once.


License
=======

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

Credits
=======

The FSA for compiling the fullform/baseform table is taken from Dawid Weiss' morfologik project

https://github.com/morfologik/morfologik-stemming

The german baseform file is a modified version of Daniel Nabers morphology file

http://www.danielnaber.de/morphologie/morphy-mapping-20110717.latin1.gz

and is distributed under CC-BY-SA http://creativecommons.org/licenses/by-sa/3.0/