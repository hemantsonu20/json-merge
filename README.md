# json-merge
A light weight library to merge two json objects into a single json object.

## Description
This library merges two json of any nested level into a single json following below logic.

* When keys are different, both keys with there value will be copied at same level.
* When keys are same at some level, following table denotes what value will be used.

    | Src / Target           | JSON Value       | JSON Array        | JSON Object       |
    |----------------------- |------------------|-------------------|-------------------|
    | JSON Value<sup>1</sup> | Src              | Src               | Src               |
    | JSON Array             | Src<sup>2</sup>  | Merge             | Src               |
    | JSON Object            | Src              | Src               | Merge<sup>3</sup> |
    
    <sup>**1**</sup> Json Value denotes boolean, number or string value in json.  
    <sup>**2**</sup> Src denotes `Src` value will be copied.  
    <sup>**3**</sup> Merge denotes both `Src` and `Target` values will be merged.
    

## Usage
This library exposes below method. It accepts source and target json and returns the merged json.
```java
public static String merge(String srcJsonStr, String targetJsonStr);
```

**Method Usage**
```java
String srcJsonStr = "{\"name\":\"json-merge\"}";
String targetJsonStr = "{\"age\":18}";
String output = JsonMerge.merge(srcJsonStr, targetJsonStr);
// {"name":"json-merge","age":18}
```

## Examples
#### Example 1
**Source Json**
```json
{
  "name": "json-merge-src"
}
```
**Target Json**
```json
{
  "name": "json-merge-target"
}
```
**Output**
```json
{
  "name": "json-merge-src"
}
```
#### Example 2
**Source Json**
```json
{
  "level1": {
    "key1": "SrcValue1"
  }
}
```
**Target Json**
```json
{
  "level1": {
    "key1": "targetValue1",
    "level2": {
      "key2": "value2"
    }
  }
}
```
  
**Output Json**
```json
{
  "level1": {
    "key1": "SrcValue1",
    "level2": {
      "key2": "value2"
    }
  }
}
```

For more examples see, [test json files](/src/test/resources/json-merge-test).   
<small>Each test json file contains a json array of three json objects, first is src, second is target and third is output json.</small>

## License
[Apache License Version 2.0](/LICENSE)
