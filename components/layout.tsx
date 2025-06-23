import type React from "react"
import { Sidebar } from "./sidebar"
import { TopNav } from "./top-nav"

export default function Layout({ children }: { children: React.ReactNode }) {
  return (
    <div className="flex h-screen bg-background">
      <Sidebar />
      <div className="flex-1 flex flex-col overflow-hidden">
        <TopNav />
        <main className="flex-1 overflow-x-hidden overflow-y-auto bg-background">{children}</main>
      </div>
    </div>
  )
}
