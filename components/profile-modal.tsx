"use client"

import { useState } from "react"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { CreditCard, Edit2, Save, Trash2 } from "lucide-react"

interface ProfileModalProps {
  isOpen: boolean
  onClose: () => void
}

interface CardInfo {
  type: string
  number: string
  expiry: string
}

interface UserProfile {
  name: string
  email: string
  company: string
  phone: string
  accountNumber: string
  bankName: string
  cards: CardInfo[]
}

export function ProfileModal({ isOpen, onClose }: ProfileModalProps) {
  const [isEditing, setIsEditing] = useState(false)
  const [userProfile, setUserProfile] = useState<UserProfile>({
    name: "Dollar Gill",
    email: "dollar@example.com",
    company: "Acme Inc.",
    phone: "+1 (555) 123-4567",
    accountNumber: "1234567890",
    bankName: "Global Bank",
    cards: [
      { type: "Visa", number: "************1387", expiry: "12/24" },
      { type: "Amex", number: "***********2468", expiry: "06/25" },
      { type: "Mastercard", number: "************9876", expiry: "03/26" },
    ],
  })

  const handleInputChange = (field: keyof UserProfile, value: string) => {
    setUserProfile((prev) => ({ ...prev, [field]: value }))
  }

  const handleSave = () => {
    setIsEditing(false)
    // Here you would typically send the updated profile to your backend
    console.log("Saving profile:", userProfile)
  }

  const handleRemoveCard = (index: number) => {
    setUserProfile((prev) => ({
      ...prev,
      cards: prev.cards.filter((_, i) => i !== index),
    }))
  }

  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent className="sm:max-w-[600px]">
        <DialogHeader>
          <DialogTitle>Profile</DialogTitle>
        </DialogHeader>
        <div className="grid gap-4 py-4">
          <div className="flex items-center gap-4">
            <Avatar className="h-20 w-20">
              <AvatarImage src="https://github.com/shadcn.png" alt={userProfile.name} />
              <AvatarFallback>
                {userProfile.name
                  .split(" ")
                  .map((n) => n[0])
                  .join("")}
              </AvatarFallback>
            </Avatar>
            <div>
              <h2 className="text-2xl font-bold">{userProfile.name}</h2>
              <p className="text-sm text-muted-foreground">{userProfile.email}</p>
            </div>
            <Button
              variant="outline"
              size="icon"
              className="ml-auto"
              onClick={() => (isEditing ? handleSave() : setIsEditing(true))}
            >
              {isEditing ? <Save className="h-4 w-4" /> : <Edit2 className="h-4 w-4" />}
            </Button>
          </div>
          <div className="grid gap-4">
            <div className="grid grid-cols-2 gap-4">
              <div>
                <Label htmlFor="name">Name</Label>
                <Input
                  id="name"
                  value={userProfile.name}
                  readOnly={!isEditing}
                  onChange={(e) => handleInputChange("name", e.target.value)}
                />
              </div>
              <div>
                <Label htmlFor="email">Email</Label>
                <Input
                  id="email"
                  value={userProfile.email}
                  readOnly={!isEditing}
                  onChange={(e) => handleInputChange("email", e.target.value)}
                />
              </div>
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div>
                <Label htmlFor="company">Company</Label>
                <Input
                  id="company"
                  value={userProfile.company}
                  readOnly={!isEditing}
                  onChange={(e) => handleInputChange("company", e.target.value)}
                />
              </div>
              <div>
                <Label htmlFor="phone">Phone</Label>
                <Input
                  id="phone"
                  value={userProfile.phone}
                  readOnly={!isEditing}
                  onChange={(e) => handleInputChange("phone", e.target.value)}
                />
              </div>
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div>
                <Label htmlFor="accountNumber">Account Number</Label>
                <Input id="accountNumber" value={userProfile.accountNumber} readOnly />
              </div>
              <div>
                <Label htmlFor="bankName">Bank Name</Label>
                <Input id="bankName" value={userProfile.bankName} readOnly />
              </div>
            </div>
          </div>
          <Card>
            <CardHeader>
              <CardTitle>Cards</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                {userProfile.cards.map((card, index) => (
                  <div key={index} className="flex items-center gap-4">
                    <CreditCard className="h-6 w-6" />
                    <div>
                      <p className="font-medium">{card.type}</p>
                      <p className="text-sm text-muted-foreground">{card.number}</p>
                    </div>
                    <p className="ml-auto text-sm">Expires: {card.expiry}</p>
                    {isEditing && (
                      <Button variant="destructive" size="icon" onClick={() => handleRemoveCard(index)}>
                        <Trash2 className="h-4 w-4" />
                      </Button>
                    )}
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
        </div>
      </DialogContent>
    </Dialog>
  )
}
