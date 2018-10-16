package sqlite_access;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DocumentSequence extends SqliteAccess {

        public String nextDocumentSequence(String sequenceType,String divisionID) throws IOException, SQLException {
            String IDBeforeUpdate="";
            String ID="";
            String sequenceTypeColumn="";
        copySqlliteToDevice();
        switch (sequenceType) {
            case "Sales":
                sequenceTypeColumn = "MaxTransactionInvoiceID";
                break;
            case "Returns":
                sequenceTypeColumn = "MaxTransactionReturnID";
                break;
            case "Sales Order":
                sequenceTypeColumn = "MaxTransactionOrderID";
                break;
            case "Warehouse Transaction":
                sequenceTypeColumn = "MaxWarehouseTransactionID";
                break;
            case "Credit Note":
                sequenceTypeColumn = "MaxTransactionCreditNote";
                break;
            case "Debit Note":
                sequenceTypeColumn = "MaxTransactionDebitNote";
                break;
            case "Payment":
                sequenceTypeColumn = "MaxTransactionPaymentID";
                break;
            case "Return Order":
                sequenceTypeColumn = "MaxReturnOrderID";
                break;
            case "Contracted FOC":
                sequenceTypeColumn = "MaxContractedFOCID";
                break;
            case "Unlimited FOC":
                sequenceTypeColumn = "MaxUnlimitedFOCID";
                break;
            case "Fixed Incentive":
                sequenceTypeColumn = "MaxFixedIncentiveID";
                break;
            case "Variable Incentive":
                sequenceTypeColumn = "MaxVariableIncentiveID";
                break;
            case "Prison Expenses":
                sequenceTypeColumn = "MaxPrisonExpensesID";
                break;
            case "Guarantees Expenses":
                sequenceTypeColumn = "MaxGuaranteesExpensesID";
                break;
            case "Gift Voucher":
                sequenceTypeColumn = "MaxGiftVoucherID";
                break;
        }
        String sqlStatement="select " + sequenceTypeColumn +" From DocumentSequence where DivisionID = " + divisionID;
        ResultSet res = querySqliteData(sqlStatement);
        IDBeforeUpdate = res.getString(1);
        Integer expectedNoOfCharachters;
        Integer actualNoOfCharachters;
        String charachterPart="";
        String numericalPart="";
        String[] splittedDocumentSequence = IDBeforeUpdate.split("[^A-Z0-9_-]+|(?<=[A-Z_-])(?=[0-9])|(?<=[0-9])(?=[A-Z_-])");

            for (int i=0;i<splittedDocumentSequence.length;i++) {
                if(i!=splittedDocumentSequence.length-1) {
                    charachterPart = charachterPart + splittedDocumentSequence[i];
                } else {
                    expectedNoOfCharachters = splittedDocumentSequence[i].length();
                    numericalPart= String.valueOf(Integer.valueOf(splittedDocumentSequence[i])+1);
                    actualNoOfCharachters = numericalPart.length();
                    while (!expectedNoOfCharachters.equals(actualNoOfCharachters)){
                        numericalPart = "0" +numericalPart;
                        actualNoOfCharachters = numericalPart.length();
                    }
                }
                ID=charachterPart+numericalPart;
            }
        return ID;
    }
}
