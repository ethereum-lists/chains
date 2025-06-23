import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Progress } from "@/components/ui/progress"

const budgetCategories = [
  { name: "Housing", spent: 2000, budget: 2500 },
  { name: "Transportation", spent: 450, budget: 500 },
  { name: "Food", spent: 800, budget: 1000 },
  { name: "Utilities", spent: 300, budget: 350 },
  { name: "Entertainment", spent: 250, budget: 300 },
]

export function BudgetTracker() {
  const totalBudget = budgetCategories.reduce((sum, category) => sum + category.budget, 0)
  const totalSpent = budgetCategories.reduce((sum, category) => sum + category.spent, 0)
  const overallPercentage = (totalSpent / totalBudget) * 100

  return (
    <Card>
      <CardHeader>
        <CardTitle>Budget Overview</CardTitle>
      </CardHeader>
      <CardContent>
        <div className="space-y-4">
          <div className="flex justify-between items-center">
            <span className="text-sm font-medium">Total Spent</span>
            <span className="text-sm font-medium">
              ${totalSpent.toLocaleString()} / ${totalBudget.toLocaleString()}
            </span>
          </div>
          <Progress value={overallPercentage} className="h-2" />
          <p className="text-sm text-muted-foreground text-right">{overallPercentage.toFixed(1)}% of budget used</p>

          <div className="space-y-2">
            {budgetCategories.map((category) => {
              const percentage = (category.spent / category.budget) * 100
              return (
                <div key={category.name} className="grid grid-cols-3 gap-2 items-center">
                  <span className="text-sm font-medium">{category.name}</span>
                  <Progress value={percentage} className="h-1.5" />
                  <span className="text-sm text-muted-foreground text-right">{percentage.toFixed(0)}%</span>
                </div>
              )
            })}
          </div>
        </div>
      </CardContent>
    </Card>
  )
}
