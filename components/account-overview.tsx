import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"

const accounts = [
  { name: "Checking", balance: 5240.23 },
  { name: "Savings", balance: 12750.89 },
  { name: "Investment", balance: 7890.45 },
]

export function AccountOverview() {
  const totalBalance = accounts.reduce((sum, account) => sum + account.balance, 0)

  return (
    <Card className="border border-border">
      <CardHeader>
        <CardTitle className="text-lg font-medium">Account Overview</CardTitle>
      </CardHeader>
      <CardContent>
        <div className="text-2xl font-semibold mb-4">${totalBalance.toFixed(2)}</div>
        <div className="space-y-2">
          {accounts.map((account) => (
            <div key={account.name} className="flex justify-between items-center">
              <span className="text-sm text-muted-foreground">{account.name}</span>
              <span className="font-medium">${account.balance.toFixed(2)}</span>
            </div>
          ))}
        </div>
      </CardContent>
    </Card>
  )
}
