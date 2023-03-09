# Create jar file
- mvn clean
- mvn package

# Build docker image
- docker build --pull --rm -f "Dockerfile" -t  language-complexity-api:latest "."

# Run docker image locally
- docker-compose up -d

# Access API

## Example 1:

URL: http://localhost:8080/v1/language/tools/wordComplexity/hi

POST Request JSON Body:
```
{
    "request": {
        "language_id": "hi",
        "text": "आपसे मिलकर अच्छा लगा"
    }
}
```

Response JSON:
```
{
    "id": "language.word.complexity.info",
    "ver": "1.0",
    "ts": "2023-03-09T10:57:32ZZ",
    "params": {
        "resmsgid": "2ec2fb5a-d5de-4eea-b8b9-04477ae2d524",
        "msgid": null,
        "err": null,
        "status": null,
        "errmsg": null
    },
    "responseCode": "OK",
    "result": {
        "text": "आपसे मिलकर अच्छा लगा",
        "wordCount": 4,
        "syllableCount": 11,
        "meanOrthoComplexity": 0.33,
        "totalOrthoComplexity": 3.6,
        "meanPhonicComplexity": 6.38,
        "totalPhonicComplexity": 70.19,
        "meanWordComplexity": 0.0,
        "totalWordComplexity": 0.0,
        "meanComplexity": 18.45,
        "wordMeasures": {
            "अच्छा": {
                "orthographic_complexity": 2.1,
                "phonologic_complexity": 31.82
            },
            "लगा": {
                "orthographic_complexity": 0.3,
                "phonologic_complexity": 8.06
            },
            "आपसे": {
                "orthographic_complexity": 0.4,
                "phonologic_complexity": 12.2
            },
            "मिलकर": {
                "orthographic_complexity": 0.8,
                "phonologic_complexity": 18.11
            }
        },
        "wordComplexityMap": {},
        "wordFrequency": {
            "अच्छा": 1,
            "लगा": 1,
            "आपसे": 1,
            "मिलकर": 1
        },
        "syllableCountMap": {
            "अच्छा": 2,
            "लगा": 2,
            "आपसे": 3,
            "मिलकर": 4
        }
    }
}
```

## Example 2:

URL: `http://localhost:8080/v1/language/tools/wordComplexity/te`

POST Request JSON Body:
```
{
    "request": {
        "language_id": "te",
        "text": "మిమ్మల్ని కలసినందుకు సంతోషంగా ఉంది"
    }
}
```

Response JSON:
```
{
    "id": "language.word.complexity.info",
    "ver": "1.0",
    "ts": "2023-03-09T10:57:28ZZ",
    "params": {
        "resmsgid": "1ff8689f-bdcc-4b00-b0c1-7703b00f0d43",
        "msgid": null,
        "err": null,
        "status": null,
        "errmsg": null
    },
    "responseCode": "OK",
    "result": {
        "text": "మిమ్మల్ని కలసినందుకు సంతోషంగా ఉంది",
        "wordCount": 4,
        "syllableCount": 15,
        "meanOrthoComplexity": 0.21,
        "totalOrthoComplexity": 3.2,
        "meanPhonicComplexity": 8.04,
        "totalPhonicComplexity": 120.57,
        "meanWordComplexity": 0.0,
        "totalWordComplexity": 0.0,
        "meanComplexity": 30.94,
        "wordMeasures": {
            "మిమ్మల్ని": {
                "orthographic_complexity": 2.8,
                "phonologic_complexity": 63.85
            },
            "కలసినందుకు": {
                "orthographic_complexity": 0.0,
                "phonologic_complexity": 28.06
            },
            "ఉంది": {
                "orthographic_complexity": 0.0,
                "phonologic_complexity": 10.3
            },
            "సంతోషంగా": {
                "orthographic_complexity": 0.4,
                "phonologic_complexity": 18.36
            }
        },
        "wordComplexityMap": {},
        "wordFrequency": {
            "మిమ్మల్ని": 1,
            "కలసినందుకు": 1,
            "ఉంది": 1,
            "సంతోషంగా": 1
        },
        "syllableCountMap": {
            "మిమ్మల్ని": 3,
            "కలసినందుకు": 6,
            "ఉంది": 2,
            "సంతోషంగా": 4
        }
    }
}
```
