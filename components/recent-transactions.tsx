import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { ArrowUpRight, ArrowDownRight } from "lucide-react"

const transactions = [
  { id: 1, name: "Amazon.com", amount: -129.99, date: "2023-07-15", type: "expense" },
  { id: 2, name: "Whole Foods Market", amount: -89.72, date: "2023-07-10", type: "expense" },
  { id: 3, name: "Netflix Subscription", amount: -15.99, date: "2023-07-05", type: "expense" },
  { id: 4, name: "Freelance Payment", amount: 750, date: "2023-07-12", type: "income" },
  { id: 5, name: "Gas Station", amount: -45.5, date: "2023-07-18", type: "expense" },
]

export function RecentTransactions() {
  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-lg font-medium">Recent Transactions</CardTitle>
      </CardHeader>
      <CardContent>
        <div className="space-y-4">
          {transactions.slice(0, 3).map((transaction) => (
            <div key={transaction.id} className="flex items-center">
              <div className="flex-1">
                <p className="text-sm font-medium">{transaction.name}</p>
                <p className="text-xs text-muted-foreground">{transaction.date}</p>
              </div>
              <div className="flex items-center">
                <span
                  className={`text-sm font-medium ${
                    transaction.type === "income"
                      ? "text-green-600 dark:text-green-400"
                      : "text-red-600 dark:text-red-400"
                  }`}
                >
                  {transaction.type === "income" ? "+" : "-"}${Math.abs(transaction.amount).toFixed(2)}
                </span>
                {transaction.type === "income" ? (
                  <ArrowUpRight className="h-4 w-4 text-green-600 dark:text-green-400 ml-1" />
                ) : (
                  <ArrowDownRight className="h-4 w-4 text-red-600 dark:text-red-400 ml-1" />
                )}
              </div>
            </div>
          ))}
        </div>
        <Button className="w-full mt-4" variant="outline">
          View All Transactions
        </Button>
      </CardContent>
    </Card>
  )
}
