"use client"

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Line, LineChart, ResponsiveContainer, Tooltip, XAxis, YAxis } from "recharts"

const data = [
  { month: "Jan", income: 2000, expenses: 1800 },
  { month: "Feb", income: 2200, expenses: 1900 },
  { month: "Mar", income: 2400, expenses: 2000 },
  { month: "Apr", income: 2600, expenses: 2200 },
  { month: "May", income: 2800, expenses: 2400 },
  { month: "Jun", income: 3000, expenses: 2600 },
]

export function FinancialChart() {
  return (
    <Card>
      <CardHeader>
        <CardTitle>Financial Overview</CardTitle>
      </CardHeader>
      <CardContent className="pb-4">
        <div className="h-[200px]">
          <ResponsiveContainer width="100%" height="100%">
            <LineChart data={data}>
              <XAxis dataKey="month" stroke="#888888" fontSize={12} tickLine={false} axisLine={false} />
              <YAxis
                stroke="#888888"
                fontSize={12}
                tickLine={false}
                axisLine={false}
                tickFormatter={(value) => `$${value}`}
              />
              <Tooltip />
              <Line type="monotone" dataKey="income" stroke="#8884d8" strokeWidth={2} />
              <Line type="monotone" dataKey="expenses" stroke="#82ca9d" strokeWidth={2} />
            </LineChart>
          </ResponsiveContainer>
        </div>
      </CardContent>
    </Card>
  )
}
