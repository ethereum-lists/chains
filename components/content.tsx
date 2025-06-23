import { List01 } from "./list01"
import { List02 } from "./list02"
import { List03 } from "./list03"

export function Content() {
  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <div className="md:col-span-2 lg:col-span-1">
        <List01 />
      </div>
      <div className="md:col-span-2 lg:col-span-1">
        <List02 />
      </div>
      <div className="md:col-span-2 lg:col-span-1">
        <List03 />
      </div>
    </div>
  )
}
