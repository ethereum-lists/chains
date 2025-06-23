import { Wallet, Plus, Send, CreditCard, MoreHorizontal } from "lucide-react"
import { Modal } from "./modal"

const accounts = [
  { name: "Checking", balance: 5240.23 },
  { name: "Savings", balance: 12750.89 },
  { name: "Investment", balance: 7890.45 },
]

export function List01() {
  const totalBalance = accounts.reduce((sum, account) => sum + account.balance, 0)

  return (
    <div className="bg-white dark:bg-gray-800 rounded-xl shadow-md p-6">
      <div className="flex items-center justify-between mb-4">
        <h2 className="text-lg font-semibold text-gray-900 dark:text-white flex items-center">
          <Wallet className="h-5 w-5 mr-2" />
          Accounts
        </h2>
        <span className="text-2xl font-bold text-gray-900 dark:text-white">${totalBalance.toFixed(2)}</span>
      </div>
      <div className="space-y-4 mb-6">
        {accounts.map((account) => (
          <div key={account.name} className="flex justify-between items-center">
            <span className="text-sm text-gray-500 dark:text-gray-400">{account.name}</span>
            <span className="font-medium text-gray-900 dark:text-white">${account.balance.toFixed(2)}</span>
          </div>
        ))}
      </div>
      <div className="grid grid-cols-2 gap-4">
        <Modal
          trigger={
            <button className="flex items-center justify-center px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors">
              <Plus className="h-4 w-4 mr-2" />
              Add
            </button>
          }
          title="Add Funds"
          description="Add funds to your account"
        />
        <Modal
          trigger={
            <button className="flex items-center justify-center px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 transition-colors">
              <Send className="h-4 w-4 mr-2" />
              Send
            </button>
          }
          title="Send Money"
          description="Send money to another account"
        />
        <Modal
          trigger={
            <button className="flex items-center justify-center px-4 py-2 bg-purple-600 text-white rounded-md hover:bg-purple-700 transition-colors">
              <CreditCard className="h-4 w-4 mr-2" />
              Top-up
            </button>
          }
          title="Top Up"
          description="Top up your account"
        />
        <button className="flex items-center justify-center px-4 py-2 bg-gray-200 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-md hover:bg-gray-300 dark:hover:bg-gray-600 transition-colors">
          <MoreHorizontal className="h-4 w-4 mr-2" />
          More
        </button>
      </div>
    </div>
  )
}
