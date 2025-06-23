export default function Loading() {
  return (
    <div className="container mx-auto py-10">
      <div className="h-9 w-32 bg-muted animate-pulse rounded mb-6" />

      <div className="space-y-4">
        <div className="grid w-full grid-cols-5 gap-2">
          {Array.from({ length: 5 }).map((_, i) => (
            <div key={i} className="h-10 bg-muted animate-pulse rounded" />
          ))}
        </div>

        <div className="h-96 bg-muted animate-pulse rounded-lg" />
      </div>
    </div>
  )
}
