import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Progress } from "@/components/ui/progress"
import { Button } from "@/components/ui/button"
import { PlusCircle } from "lucide-react"

const savingsGoals = [
  { name: "Emergency Fund", current: 10000, target: 25000 },
  { name: "Vacation", current: 3000, target: 5000 },
  { name: "New Car", current: 15000, target: 35000 },
]

export function SavingsGoals() {
  return (
    <Card>
      <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
        <CardTitle className="text-md font-medium">Savings Goals</CardTitle>
        <Button variant="outline" size="icon">
          <PlusCircle className="h-4 w-4" />
          <span className="sr-only">Add new savings goal</span>
        </Button>
      </CardHeader>
      <CardContent>
        <div className="space-y-4">
          {savingsGoals.map((goal) => {
            const percentage = (goal.current / goal.target) * 100
            return (
              <div key={goal.name} className="space-y-2">
                <div className="flex justify-between text-sm">
                  <span>{goal.name}</span>
                  <span>
                    ${goal.current.toLocaleString()} / ${goal.target.toLocaleString()}
                  </span>
                </div>
                <Progress value={percentage} className="h-2" />
                <p className="text-xs text-right text-muted-foreground">{percentage.toFixed(1)}% complete</p>
              </div>
            )
          })}
        </div>
      </CardContent>
    </Card>
  )
}
