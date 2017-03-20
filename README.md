# TransACT
[![Build Status](https://travis-ci.org/Ledger-Software/Ledger.svg?branch=master)](https://travis-ci.org/Ledger-Software/Ledger)
[![codebeat badge](https://codebeat.co/badges/66645fcb-9975-428c-8a9c-251c02a5a968)](https://codebeat.co/projects/github-com-ledger-software-ledger)

TransACT is a finance management application the provides users with an easy and intuitive way to track their finances. Users are able to export the financial history from their bank or financial institution and them import it into TransACT. The program will then aggregate this data into one convenient place that is easy for a user to visually comprehend. Users can view their spending history at a glance or filter by a specific account to get a more in depth view. TransACT also provides charts and statistics to help the user analyze trends in their spending. Transactions can be sorted into categories that can be used to provide additional analysis. All data stored is heavily encrypted so that sensitive financial data is never at risk.

## Code Pipeline
The TransACT software is developed using a set process that ensures the integrity of the code. Each feature is 
 developed on its own branch named after the convention "f_featureName". Upon each git commit, 
 [Travis CI](https://travis-ci.org/Ledger-Software/Ledger) runs checks that ensure that the codebase successfully
 builds and that all tests pass. Once the feature is completely implemented and all Travis checks pass 
 successfully, a pull request is then opened for the feature. If the feature is to be merged into master, at 
 least one peer has to complete a code review and approve the changes, as the master branch has locks on it 
 that prevent anyone from tampering with it without approval from the team. A standard rule is that feature has 
 to have two approved peer code reviews before it is merged into the master branch.


