function PlanCard({ plan, ...props }) {
    return (
        <div className={`card h-100 ${props.className}`}>
            <div className="card-body">
                <h5 className="card-title">{plan.name}</h5>
                <p className="card-text">
                    Table count: {plan.maxTableCount},
                    month price: {plan.monthPrice} {plan.currency}
                </p>
                {(plan.upfrontDiscount6m || plan.upfrontDiscount1y) && <div className="card-text">
                    Upfron payment discount:
                    <ul>
                        {plan.upfrontDiscount6m && <li>6 months: {plan.upfrontDiscount6m}%</li>}
                        {plan.upfrontDiscount1y && <li>1 year: {plan.upfrontDiscount1y}%</li>}
                    </ul>
                </div>}
                <p className="card-text">{plan.description}</p>
            </div>
        </div>
    )
}

export default PlanCard