/*
 * This file is part of Bisq.
 *
 * Bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bisq. If not, see <http://www.gnu.org/licenses/>.
 */

package bisq.desktop.components.paymentmethods;

import bisq.desktop.components.InputTextField;
import bisq.desktop.util.FormBuilder;
import bisq.desktop.util.Layout;
import bisq.desktop.util.validation.InteracETransferValidator;

import bisq.core.locale.Res;
import bisq.core.locale.TradeCurrency;
import bisq.core.payment.AccountAgeWitnessService;
import bisq.core.payment.InteracETransferAccount;
import bisq.core.payment.PaymentAccount;
import bisq.core.payment.payload.InteracETransferAccountPayload;
import bisq.core.payment.payload.PaymentAccountPayload;
import bisq.core.util.BSFormatter;
import bisq.core.util.validation.InputValidator;

import javafx.scene.layout.GridPane;

public class InteracETransferForm extends PaymentMethodForm {

    private final InteracETransferAccount interacETransferAccount;
    private final InteracETransferValidator interacETransferValidator;
    private InputTextField mobileNrInputTextField;

    public static int addFormForBuyer(GridPane gridPane, int gridRow,
                                      PaymentAccountPayload paymentAccountPayload) {
        FormBuilder.addTopLabelTextField(gridPane, ++gridRow, Res.get("payment.account.owner"),
                ((InteracETransferAccountPayload) paymentAccountPayload).getHolderName());
        FormBuilder.addTopLabelTextField(gridPane, ++gridRow, Res.get("payment.emailOrMobile"),
                ((InteracETransferAccountPayload) paymentAccountPayload).getEmail());
        FormBuilder.addTopLabelTextField(gridPane, ++gridRow, Res.get("payment.secret"),
                ((InteracETransferAccountPayload) paymentAccountPayload).getQuestion());
        FormBuilder.addTopLabelTextField(gridPane, ++gridRow, Res.get("payment.answer"),
                ((InteracETransferAccountPayload) paymentAccountPayload).getAnswer());
        return gridRow;
    }

    public InteracETransferForm(PaymentAccount paymentAccount, AccountAgeWitnessService accountAgeWitnessService, InteracETransferValidator interacETransferValidator,
                                InputValidator inputValidator, GridPane gridPane, int gridRow, BSFormatter formatter) {
        super(paymentAccount, accountAgeWitnessService, inputValidator, gridPane, gridRow, formatter);
        this.interacETransferAccount = (InteracETransferAccount) paymentAccount;
        this.interacETransferValidator = interacETransferValidator;
    }

    @Override
    public void addFormForAddAccount() {
        gridRowFrom = gridRow + 1;

        InputTextField holderNameInputTextField = FormBuilder.addInputTextField(gridPane, ++gridRow,
                Res.get("payment.account.owner"));
        holderNameInputTextField.setValidator(inputValidator);
        holderNameInputTextField.textProperty().addListener((ov, oldValue, newValue) -> {
            interacETransferAccount.setHolderName(newValue);
            updateFromInputs();
        });

        mobileNrInputTextField = FormBuilder.addInputTextField(gridPane, ++gridRow, Res.get("payment.emailOrMobile"));
        mobileNrInputTextField.setValidator(interacETransferValidator);
        mobileNrInputTextField.textProperty().addListener((ov, oldValue, newValue) -> {
            interacETransferAccount.setEmail(newValue);
            updateFromInputs();
        });

        InputTextField questionInputTextField = FormBuilder.addInputTextField(gridPane, ++gridRow, Res.get("payment.secret")).second;
        questionInputTextField.setValidator(interacETransferValidator.questionValidator);
        questionInputTextField.textProperty().addListener((ov, oldValue, newValue) -> {
            interacETransferAccount.setQuestion(newValue);
            updateFromInputs();
        });

        InputTextField answerInputTextField = FormBuilder.addInputTextField(gridPane, ++gridRow, Res.get("payment.answer")).second;
        answerInputTextField.setValidator(interacETransferValidator.answerValidator);
        answerInputTextField.textProperty().addListener((ov, oldValue, newValue) -> {
            interacETransferAccount.setAnswer(newValue);
            updateFromInputs();
        });
        TradeCurrency singleTradeCurrency = interacETransferAccount.getSingleTradeCurrency();
        String nameAndCode = singleTradeCurrency != null ? singleTradeCurrency.getNameAndCode() : "null";
        FormBuilder.addTopLabelTextField(gridPane, ++gridRow, Res.get("shared.currency"),
                nameAndCode);
        addLimitations();
        addAccountNameTextFieldWithAutoFillToggleButton();
    }

    @Override
    protected void autoFillNameTextField() {
        setAccountNameWithString(mobileNrInputTextField.getText());
    }

    @Override
    public void addFormForDisplayAccount() {
        gridRowFrom = gridRow;
        FormBuilder.addTopLabelTextField(gridPane, gridRow, Res.get("payment.account.name"),
                interacETransferAccount.getAccountName(), Layout.FIRST_ROW_AND_GROUP_DISTANCE);
        FormBuilder.addTopLabelTextField(gridPane, ++gridRow, Res.get("shared.paymentMethod"),
                Res.get(interacETransferAccount.getPaymentMethod().getId()));
        FormBuilder.addTopLabelTextField(gridPane, ++gridRow, Res.get("payment.account.owner"),
                interacETransferAccount.getHolderName());
        FormBuilder.addTopLabelTextField(gridPane, ++gridRow, Res.get("payment.email"),
                interacETransferAccount.getEmail()).second.setMouseTransparent(false);
        FormBuilder.addTopLabelTextField(gridPane, ++gridRow, Res.get("payment.secret"),
                interacETransferAccount.getQuestion()).second.setMouseTransparent(false);
        FormBuilder.addTopLabelTextField(gridPane, ++gridRow, Res.get("payment.answer"),
                interacETransferAccount.getAnswer()).second.setMouseTransparent(false);
        TradeCurrency singleTradeCurrency = interacETransferAccount.getSingleTradeCurrency();
        String nameAndCode = singleTradeCurrency != null ? singleTradeCurrency.getNameAndCode() : "null";
        FormBuilder.addTopLabelTextField(gridPane, ++gridRow, Res.get("shared.currency"),
                nameAndCode);
        addLimitations();
    }

    @Override
    public void updateAllInputsValid() {
        allInputsValid.set(isAccountNameValid()
                && interacETransferValidator.validate(interacETransferAccount.getEmail()).isValid
                && inputValidator.validate(interacETransferAccount.getHolderName()).isValid
                && interacETransferValidator.questionValidator.validate(interacETransferAccount.getQuestion()).isValid
                && interacETransferValidator.answerValidator.validate(interacETransferAccount.getAnswer()).isValid
                && interacETransferAccount.getTradeCurrencies().size() > 0);
    }
}
