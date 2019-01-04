package com.capgemini.mmBankController;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Comparator;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.TreeSet;
import com.moneymoney.account.SavingsAccount;
import com.moneymoney.account.service.SavingsAccountService;
import com.moneymoney.account.service.SavingsAccountServiceImpl;
import com.moneymoney.account.util.DBUtil;
import com.moneymoney.exception.AccountNotFoundException;

@WebServlet("*.mm")
public class MmBankAccountController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private RequestDispatcher dispatcher;
	boolean flag = false;

	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		super.init();

		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/bankapp_db", "root", "root");
			PreparedStatement preparedStatement = connection
					.prepareStatement("DELETE FROM ACCOUNT");
			preparedStatement.execute();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public MmBankAccountController() {
		super();

	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		SavingsAccountService savingsAccountService = new SavingsAccountServiceImpl();
		PrintWriter out = response.getWriter();
		String path = request.getServletPath();
		SavingsAccount savingsAccount = null, savingsAccount1 = null;
		int accountNumber = 0;

		switch (path) {
		case "/addsnewsavingsacountForm.mm":
			response.sendRedirect("savingsAccount.html");
			break;
		case "/addNewSavingsAccount.mm":
			String accountHolderName = request.getParameter("holderName");
			double accountBalance = Double.parseDouble(request
					.getParameter("accountBalance"));
			boolean acountType = request.getParameter("salary")
					.equalsIgnoreCase("yes") ? false : true;
			try {
				savingsAccountService.createNewAccount(accountHolderName,
						accountBalance, acountType);
				response.sendRedirect("getAll.mm");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;

		case "/getAll.mm":
			try {
				List<SavingsAccount> accounts = savingsAccountService
						.getAllSavingsAccount();
				request.setAttribute("accounts", accounts);
				dispatcher = request.getRequestDispatcher("AccountDetails.jsp");
				dispatcher.forward(request, response);
			} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;

		case "/searchForm.mm":
			response.sendRedirect("SearchForm.jsp");
			break;
		case "/search.mm":
			int accountNumberForSearch = Integer.parseInt(request
					.getParameter("txtAccountNumber"));
			try {
				SavingsAccount account = savingsAccountService
						.getAccountById(accountNumberForSearch);
				request.setAttribute("account", account);
				dispatcher = request.getRequestDispatcher("AccountDetails.jsp");
				dispatcher.forward(request, response);
			} catch (ClassNotFoundException | SQLException
					| AccountNotFoundException e) {
				e.printStackTrace();
			}
			break;

		case "/updateForm.mm":
			response.sendRedirect("update.html");
			break;
		case "/updateAccount.mm":
			int accountNumbr = Integer.parseInt(request
					.getParameter("accountnumber"));
			try {
				SavingsAccount accountUpdate = savingsAccountService
						.getAccountById(accountNumbr);
				request.setAttribute("accounts", accountUpdate);
				dispatcher = request
						.getRequestDispatcher("updateAccountDetails.jsp");
				dispatcher.forward(request, response);
			} catch (ClassNotFoundException | SQLException
					| AccountNotFoundException e) {
				e.printStackTrace();
			}
			break;

		case "/updateAccountDetails.mm":
			int accountId = Integer.parseInt(request.getParameter("Number"));
			SavingsAccount accountUpdate;
			try {
				accountUpdate = savingsAccountService.getAccountById(accountId);
				String accHName = request.getParameter("AccountHolderName");
				accountUpdate.getBankAccount().setAccountHolderName(accHName);
				double accBal = Double.parseDouble(request
						.getParameter("Balance"));
				boolean isSalary = request.getParameter("salary")
						.equalsIgnoreCase("no") ? false : true;
				accountUpdate.setSalary(isSalary);
				savingsAccountService.updateAccount(accountUpdate);
				response.sendRedirect("getAll.mm");
			} catch (ClassNotFoundException | SQLException
					| AccountNotFoundException e) {
				e.printStackTrace();
			}
			break;

		case "/checkCurrentBalanceForm.mm":
			response.sendRedirect("checkCurrentBalance.html");
			break;
		case "/checkCurrentBalance.mm":
			int accountNumber1 = Integer.parseInt(request
					.getParameter("accountNumber"));
			try {
				savingsAccountService.checkBalance(accountNumber1);
				out.println(savingsAccountService.checkBalance(accountNumber1));
			} catch (AccountNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;

		case "/sortByName.mm":
			flag = !flag;
			try {
				Collection<SavingsAccount> accounts = savingsAccountService
						.getAllSavingsAccount();
				Set<SavingsAccount> accountSet = new TreeSet<>(
						new Comparator<SavingsAccount>() {

							@Override
							public int compare(SavingsAccount arg0,
									SavingsAccount arg1) {
								int result = arg0
										.getBankAccount()
										.getAccountHolderName()
										.compareTo(
												arg1.getBankAccount()
														.getAccountHolderName());
								if (flag == true) {
									return result;
								} else {
									return -result;
								}
							}
						});
				accountSet.addAll(accounts);
				request.setAttribute("accounts", accountSet);
				dispatcher = request.getRequestDispatcher("AccountDetails.jsp");
				dispatcher.forward(request, response);
			} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			break;

		case "/sortByBalance.mm":
			flag = !flag;
			try {
				Collection<SavingsAccount> accounts = savingsAccountService
						.getAllSavingsAccount();
				Set<SavingsAccount> accountSet = new TreeSet<>(
						new Comparator<SavingsAccount>() {
							@Override
							public int compare(SavingsAccount arg0,
									SavingsAccount arg1) {
								int result = (int) (arg0.getBankAccount()
										.getAccountBalance() - (arg1
										.getBankAccount().getAccountBalance()));
								if (flag == true) {
									return result;
								} else {
									return -result;
								}
							}
						});
				accountSet.addAll(accounts);
				request.setAttribute("accounts", accountSet);
				dispatcher = request.getRequestDispatcher("AccountDetails.jsp");
				dispatcher.forward(request, response);
			} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			break;

		case "/closeAccountForm.mm":
			response.sendRedirect("delete.html");
			break;
		case "/deleteAccount.mm":
			int accountNumberDelete = Integer.parseInt(request
					.getParameter("accountNumber"));
			try {
				savingsAccountService.deleteAccount(accountNumberDelete);
				DBUtil.commit();
				response.sendRedirect("getAll.mm");
			} catch (AccountNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;

		case "/withdrawAmountForm.mm":
			response.sendRedirect("withdraw.html");
			break;
		case "/withdrawFromAccount.mm":
			int accountNumberToWithdraw = Integer.parseInt(request
					.getParameter("accountNumber"));
			double amountToWithdraw = Double.parseDouble(request
					.getParameter("amount"));
			try {
				savingsAccount = savingsAccountService
						.getAccountById(accountNumberToWithdraw);
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (AccountNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			try {
				savingsAccountService
						.withdraw(savingsAccount, amountToWithdraw);
				DBUtil.commit();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			break;

		case "/depositForm.mm":
			response.sendRedirect("deposit.html");
			break;
		case "/depositFormAccount.mm":
			int accountNumberToDeposit = Integer.parseInt(request
					.getParameter("accountNumber"));
			double amountToDeposit = Double.parseDouble(request
					.getParameter("amount"));
			try {
				savingsAccount = savingsAccountService
						.getAccountById(accountNumberToDeposit);
			} catch (ClassNotFoundException | SQLException
					| AccountNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				savingsAccountService.deposit(savingsAccount, amountToDeposit);
				DBUtil.commit();
			} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;

		case "/fundTransferForm.mm":
			response.sendRedirect("fundTransfer.html");
			break;
		case "/fundTransfer.mm":
			int sendersAccountNumber = Integer.parseInt(request
					.getParameter("sendersAccountNumber"));
			int receiversAccountNumber = Integer.parseInt(request
					.getParameter("receiversAccountNumber"));
			double amountToTransfer = Double.parseDouble(request
					.getParameter("amount"));
			try {
				savingsAccount = savingsAccountService
						.getAccountById(sendersAccountNumber);
				savingsAccount1 = savingsAccountService
						.getAccountById(receiversAccountNumber);
			} catch (ClassNotFoundException | SQLException
					| AccountNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				savingsAccountService.fundTransfer(savingsAccount,
						savingsAccount1, amountToTransfer);
				DBUtil.commit();
			} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;

		}
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

	}
}