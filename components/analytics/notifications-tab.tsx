"use client"

import { useState } from "react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Switch } from "@/components/ui/switch"
import { Bell, AlertTriangle, TrendingUp, TrendingDown, DollarSign, Users } from "lucide-react"

const notificationTypes = [
  { id: "account", label: "Account Activity", icon: Bell },
  { id: "security", label: "Security Alerts", icon: AlertTriangle },
  { id: "performance", label: "Performance Updates", icon: TrendingUp },
  { id: "market", label: "Market Trends", icon: TrendingDown },
  { id: "financial", label: "Financial Reports", icon: DollarSign },
  { id: "user", label: "User Behavior", icon: Users },
]

export function NotificationsTab() {
  const [notifications, setNotifications] = useState({
    account: true,
    security: true,
    performance: false,
    market: false,
    financial: true,
    user: false,
  })

  const toggleNotification = (id) => {
    setNotifications((prev) => ({ ...prev, [id]: !prev[id] }))
  }

  return (
    <div className="space-y-4">
      <Card>
        <CardHeader>
          <CardTitle className="text-xl font-semibold">Notification Preferences</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          {notificationTypes.map((type) => (
            <div key={type.id} className="flex items-center justify-between">
              <div className="flex items-center space-x-4">
                <type.icon className="h-5 w-5 text-muted-foreground" />
                <span className="text-sm font-medium">{type.label}</span>
              </div>
              <Switch checked={notifications[type.id]} onCheckedChange={() => toggleNotification(type.id)} />
            </div>
          ))}
        </CardContent>
      </Card>
      <Card>
        <CardHeader>
          <CardTitle className="text-xl font-semibold">Recent Notifications</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="flex items-center space-x-4">
            <AlertTriangle className="h-5 w-5 text-yellow-500" />
            <div>
              <p className="text-sm font-medium">Unusual account activity detected</p>
              <p className="text-xs text-muted-foreground">2 hours ago</p>
            </div>
          </div>
          <div className="flex items-center space-x-4">
            <TrendingUp className="h-5 w-5 text-green-500" />
            <div>
              <p className="text-sm font-medium">Your portfolio has grown by 5% this week</p>
              <p className="text-xs text-muted-foreground">1 day ago</p>
            </div>
          </div>
          <div className="flex items-center space-x-4">
            <Bell className="h-5 w-5 text-blue-500" />
            <div>
              <p className="text-sm font-medium">New feature: Advanced analytics now available</p>
              <p className="text-xs text-muted-foreground">3 days ago</p>
            </div>
          </div>
          <div className="flex items-center space-x-4">
            <DollarSign className="h-5 w-5 text-purple-500" />
            <div>
              <p className="text-sm font-medium">Monthly financial report is ready for review</p>
              <p className="text-xs text-muted-foreground">5 days ago</p>
            </div>
          </div>
        </CardContent>
      </Card>
      <div className="flex justify-end">
        <Button variant="outline" className="text-sm">
          View All Notifications
        </Button>
      </div>
    </div>
  )
}
