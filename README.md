# Split Ingest PDF

Realiza importação de arquivos PDF para o Elasticsearch. Os arquivos são carregados **inteiros** ou **divididos em páginas**.

## Apenas um arquivo PDF

![Carga de PDF único](https://github.com/viniciuslj/split-ingest-pdf/blob/master/images/single-mode.png?raw=true)
```bash
(...) -f arquivo.pdf
```

## Modo recursivo no diretório

![Carga recursiva de PDF](https://github.com/viniciuslj/split-ingest-pdf/blob/master/images/recursive-mode.png?raw=true)
```bash
(...) -r diretorio/de/pdf
```

## Como usar

```bash
java -jar split-ingest-pdf.jar [-s] [-c <cluster name>] [-f <pdf file>] [-h <elasticsearch host>] [-H] -i <index name> [-p <elasticsearch port>] [-r <recursive path>]
```

Comando                               | Descrição
--------------------------------------|-------------------------------------------
|-s,--split                           |Divide os arquivos por página
|-c,--cluster <cluster name>          |Nome do cluster (Default "docker-cluster")
|-f,--pdf-file <pdf file>             |Nome do arquivo PDF (Ex.: diretorio/arquivo.pdf)
|-h,--host <elasticsearch host>       |Endereço dp Elasticsearch (Default "localhost")
|-H,--help                            |Exibe a ajuda
|-i,--index <index name>              |Nome do índice no Elasticsearch
|-p,--port <elasticsearch port>       |Porta TCP do Elasticsearch (Default "9300")
|-r,--recursive-path <recursive path> |Diretório inicial dos arquivos para processamento recursivo Ex.: diretorio/subdiretorio)

## Passo a passo

> **Requisitos**
> - [JDK - Java Development Kit](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
> - [Maven](https://maven.apache.org/)
> - [Git](https://git-scm.com/downloads) (ou faça download do código [aqui](https://github.com/viniciuslj/split-ingest-pdf/archive/master.zip))
<br>


```bash
# Download do código fonte
git clone https://github.com/viniciuslj/split-ingest-pdf

# Entrando no diretório baixado
cd split-ingest-pdf

# Executando a compilação
mvn package
```

O processo de compilação vai gerar o `split-ingest-pdf.jar` no diretório `target`.
Importe apenas um PDF ou uma estrutura de diretórios no modo recursivo.
Pode ser definido `-s` para divisão dos arquivos PDF's em páginas. Dessa forma será criado um 
[documento](https://www.elastic.co/guide/en/elasticsearch/reference/current/_basic_concepts.html#_document) 
no Elasticsearch para cada página, entretanto esses documnetos podem ser relacionados.

<br>

> Caso o computador não possua memória RAM/Swap suficiente e o arquivo PDF seja grande, a importação pode falhar.

## Exemplos

> O índice será criado, caso ainda não exista no Elasticsearch.<br>
> Apenas arquivos *.pdf serão considerados quando em modo recursivo.

```bash
# Dividir o PDF manual-linux.pdf em páginas, extrair o texto, utilizar o índice "livros",
# enviar para o ES em localhost (127.0.0.1), porta 9300 e cluster "docker-cluster"
java -jar split-ingest-pdf.jar -s -i livros -f livros/linux/manual-linux.pdf
```
```bash
# Extrair o texto do arquivo reference.pdf (sem divisão de páginas), utilizar o índice "livros",
# enviar para o ES em localhost (127.0.0.1), porta 9300 e cluster "docker-cluster"
java -jar split-ingest-pdf.jar -i livros -f livros/java/reference.pdf
```
```bash
# Dividir em páginas todos os PDF's do diretório "livros" e seus subdiretórios, 
# extrair o texto, utilizar o índice "livros", enviar para o ES em 
# localhost (127.0.0.1), porta 9300 e cluster "docker-cluster"
java -jar split-ingest-pdf.jar -s -i livros -r livros
```
```bash
# Elasticsearch em 170.26.2.89
java -jar split-ingest-pdf.jar -s -i livros -r livros -h 170.26.2.89
```
```bash
# Elasticsearch em banco-elastic.meusite.com.br
java -jar split-ingest-pdf.jar -s -i livros -r /home/usuario/livros -h banco-elastic.meusite.com.br
```

## Estrutura do [documento](https://www.elastic.co/guide/en/elasticsearch/reference/current/_basic_concepts.html#_document) no Elasticsearch

### PDF divido em páginas (`-s`)
```javascript
{
    "_index": "livros",
    "_type": "_doc",
    "_id": "/home/usuario/livros/linux/manual-linux.pdf.1",
    "_source": {
        "parent": "linux",
        "fileName": "manual-linux.pdf",
        "absolutePath": "/home/usuario/livros/linux/manual-linux.pdf",
        "page": 1,
        "parentParent": "livros",
        "directory": "/home/usuario/livros/linux",
        "content": "Texto plano extraído do PDF..."
    }
}
```
### PDF não divido
Mesma estrutura, porém em `_id` é omitida a página após a extensão `.pdf` e é adicionado um campo de quantidade de páginas.

## Tecnologias envolvidas
- [ElasticSearch](https://www.elastic.co/)
- [Java](https://www.java.com/pt_BR/)
- [Apache PDFBox](https://pdfbox.apache.org/)
- [Maven](https://maven.apache.org/)
- [Git](https://git-scm.com/downloads)
- [Apache Commons CLI](https://commons.apache.org/proper/commons-cli/)
- [Elasticsearch Transport Client](https://www.elastic.co/guide/en/elasticsearch/client/java-api/current/transport-client.html)

> O Transport Client será removido do Elasticsearch em versões futuras [(Link)](https://www.elastic.co/guide/en/elasticsearch/client/java-api/current/java-api.html).<br>
> Já está disponível o [Java High Level REST Clien](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/6.3/java-rest-high.html). <br>
> Em breve o split-ingest-pdf será atualizado.

## Implementação
- A implementação foi realizada em [Java](https://www.java.com/pt_BR/).
- O gerenciamento de dependências e automação da compilação foi realizado com o [Maven](https://maven.apache.org/).
- O reconhecimento e validação dos parâmetros da linha de comando foi apoiado pela library [Apache Commons CLI](https://commons.apache.org/proper/commons-cli/).
- A manipulação de PDF foi feita com o [Apache PDFBox](https://pdfbox.apache.org/).
- A interação com o servidor do Elasticsearch é feita por meio da API [Elasticsearch Transport Client](https://www.elastic.co/guide/en/elasticsearch/client/java-api/current/transport-client.html).

### Diagrama de Classes

![Diagrama de Classes](https://github.com/viniciuslj/split-ingest-pdf/blob/master/documentation/ClassDiagram.png?raw=true)

**App**: Fluxo de execução e interação com as demais classes.

**CommandLineOptions**: Interpreta os parâmetros da linha de comando .

**InputFiles**: Interação com o sistema de arquivos.

**PDFManipulator**: Manipulação do PDF para divisão em páginas e extração do texto.

**ElasticsearchClient**: Realiza conexão com o servidor do Elasticsearch para envio dos textos extraídos.
