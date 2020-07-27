# Insight2020_ConsumerComplaints
A Java solution to the Insight Data Engineering coding challenge 2020. 

## Problem
The federal government provides a way for consumers to file complaints against companies regarding different financial products, such as payment problems with a credit card or debt collection tactics. This challenge will be about identifying the number of complaints filed and how they're spread across different companies. 

**For this challenge, we want to know for each financial product and year, the total number of complaints, number of companies receiving a complaint, and the highest percentage of complaints directed at a single company.

## Problem Solving Approach
To satisfy the problem's requirements while optimizing for speed, we adopt a straightforward approach and using/working as little of the inputs as possible to get the desired output. The approach is such:
#### 1). Figure out, from header, which column indices are associated with 3 items: product, date received, company. These 3 pieces of information, per complaint, is the minimal amount of information needed to generate the desired output
#### 2). Iterative through each line, accounting for unwanted indentation and general syntax irregularities within the "consumer complaint narrative" field, and condense down each complaint item into a string concated by the 3 pieces of information aforementioned (product, date received, company).
#### 3). Given this condensed arrayList of strings, we sort it alphabetically
#### 4). We iterate through this sorted/condensed string to generate the desired output
### Syntax Irregularities
Various syntax irregularities can appear in "consumer complaint narrative" field. The following rules/assumptions are adhered to to overcome them:
#### 1). For complaints spamming multiple lines, the code only focuses on finding the *starting quotation* and *ending quotation*. As long as these 2 items are not found, any other syntax features encountered are ignored and the complaint is considered "incomplete" from the perspective of retrieving the raw data needed to generate an additional element of the condensed string of (4) mentioned above. Because "company" in the example is a field that happens after "consumer feedback".
#### 2). The above approach however, does run into problems when consumers add their own "quotation" marks into the narrative. In this case a more meticulous method of identifying a "true" or "false" quotation is needed. Within the scope of this problem, for each quotation encountered, the code will check the position before and after the quotation (after checking for index validity) to see if a "," character exists. If it does, then the quotation is considered "valid" or "true". This is also a very human approach when scanning through line items to identify whether the quotation is part of the consumer complaint or true signal that we're going into the next field.


## Language/Libraries Used
Java is used here, with SDK 14.0.2. No other external dependencies/libraries used.
